package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.visitor.ClassVisitor
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Created by António Bastião on 02.11.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class ProtoDataStoreWriter(
    output: OutputStream,
    private val declaration: KSClassDeclaration,
    logger: KSPLogger,
) : IWriter {

    private val outputStreamWriter: OutputStreamWriter = OutputStreamWriter(output)
    private val accumulator: IAccumulator = Accumulator()

    init {
        logger.info("ProtoDataStoreWriter: init processing")

        declaration.containingFile?.accept(ClassVisitor(logger), this)

    }

    override fun writeFunction(function: KSFunctionDeclaration) {
        declaration.getAllFunctions()
            .filter { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            .filter { declaration -> declaration.isAbstract }
            .filter { declaration -> declaration.parameters.size == 1 }.forEach { declaration ->
                declaration.parameters.first().let { parameter ->

                    parameter.type.takeIf { it.isFunctionTypeWithBuilderArgument }
                        ?.let { typeReference ->
                            typeReference.element!!.typeArguments.map {
                                it.type.toString()
                            }.single { it != "Builder" }.let {
                                accumulator.constructorArg = it
                                accumulateImport(it, parameter)
                                "$it.Builder"
                            }.also {
                                accumulateImport(it, parameter)
                            }
                        } ?: parameter.type.element!!.typeArguments.first().type.also {
                        accumulateImport(it.toString(), parameter)
                    }

                    val isBuilder: Boolean = parameter.type.isFunctionTypeWithBuilderArgument

                    val functionBody = if (isBuilder) "it.toBuilder()" else "it"

                    val paramType: String = parameter.type.toString()

                    accumulator.storeFunction(
                        """    override suspend fun ${parameter.parent}(${parameter}: ${paramType}){
                        |        dataStore.updateData {
                        |            ${parameter}($functionBody)
                        |        }
                        |    }
                        """.trimMargin()
                    )

                }
            }
    }

    private fun accumulateImport(
        it: String,
        parameter: KSValueParameter
    ) {
        val argImport = parameter.containingFile?.packageName?.getQualifier()!! + "." + it
        accumulator.storeImport(argImport)
    }

    override fun writeProperty(property: KSPropertyDeclaration) {

        fun getPropertyType(returnType: KSType) = returnType.toString()
            .replace("[Error type: Unresolved type for ", "")
            .replace("]", "")

        declaration.getAllProperties()
            .filter { declaration -> declaration.isAbstract() }
            .filter { declaration ->
                declaration.type.toString() == "Flow"
            }
            .also {
                accumulator.storeImport("kotlinx.coroutines.flow.Flow")
            }
            .forEach {

                val returnType = it.type.resolve()

                val declaredProperty =
                    "    override val $it: ${getPropertyType(returnType)} = dataStore.data"

                accumulator.storeProperty(declaredProperty)
            }
    }

    override fun write() {

        outputStreamWriter.write("package ${declaration.packageName.asString()}")
        outputStreamWriter.appendLine().appendLine()
        accumulator.storeImport("androidx.datastore.core.DataStore")

        outputStreamWriter.appendLine()
        accumulator.importSet.forEach {
            outputStreamWriter.write(it)
            outputStreamWriter.appendLine()
        }

        outputStreamWriter.appendLine().appendLine()

        val fileName = declaration.simpleName.asString()
        val className = "${fileName}Impl"

        outputStreamWriter.write("class $className(private val dataStore: DataStore<${accumulator.constructorArg}>) : $fileName {\n")

        outputStreamWriter.appendLine().appendLine()

        accumulator.propertySet.forEach {
            outputStreamWriter.write(it)
            outputStreamWriter.appendLine().appendLine()
        }

        accumulator.functionSet.forEach {
            outputStreamWriter.write(it)
            outputStreamWriter.appendLine().appendLine()
        }

        outputStreamWriter.write("}")
        outputStreamWriter.close()
    }
}

private val KSTypeReference.isFunctionTypeWithBuilderArgument: Boolean
    get() = takeIf { it.resolve().isFunctionType }
        ?.element!!.typeArguments
        .map {
            it.type.toString()
        }.contains("Builder")