package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.processor.ClassVisitor
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
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
    private val logger: KSPLogger,
    private val accumulator: IAccumulator,
) : IWriter {

    private val writer: OutputStreamWriter

    init {
        logger.info("ProtoDataStoreWriter: init processing")
        writer = OutputStreamWriter(output)
    }

    override fun writeFunction(classDeclaration: KSClassDeclaration) {
        classDeclaration.getAllFunctions()
            .filter { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            .filter { declaration -> declaration.isAbstract }
            .filter { declaration -> declaration.parameters.size == 1 }.forEach { declaration ->
                declaration.parameters.first().let { parameter ->

                    parameter.type.takeIf { it.isFunctionTypeWithBuilderArgument }?.let {
                        it.element!!.typeArguments.map {
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

    override fun writeProperty(classDeclaration: KSClassDeclaration) {
        logger.warn("writeProperty not implemented")
    }

    override fun write() {
        val visitor = ClassVisitor(logger, this)

        declaration.containingFile?.accept(visitor, writer)

        writer.write("package ${declaration.packageName.asString()}")
        writer.appendLine().appendLine()
        accumulator.storeImport("androidx.datastore.core.DataStore")

        writer.appendLine()
        accumulator.importSet.forEach {
            writer.write(it)
            writer.appendLine()
        }

        writer.appendLine().appendLine()

        val fileName = declaration.simpleName.asString()
        val className = "${fileName}Impl"

        writer.write("class $className(private val dataStore: DataStore<${accumulator.constructorArg}>) : $fileName {\n")

        writer.appendLine().appendLine()

        accumulator.propertySet.forEach {
            writer.write(it)
            writer.appendLine().appendLine()
        }

        accumulator.functionSet.forEach {
            writer.write(it)
            writer.appendLine().appendLine()
        }

        writer.write("}")
        writer.close()
    }
}

val KSTypeReference.isFunctionTypeWithBuilderArgument: Boolean
    get() = takeIf { it.resolve().isFunctionType }
        ?.element!!.typeArguments
        .map {
            it.type.toString()
        }.contains("Builder")