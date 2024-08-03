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

/**
 * Created by António Bastião on 02.11.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class ProtoDataStoreWriter(
    output: OutputStream,
    private val declaration: KSClassDeclaration,
    private val logger: KSPLogger,
) : BaseWriter(output) {

    init {
        logger.info("ProtoDataStoreWriter init processing")

        declaration.containingFile?.accept(ClassVisitor(logger), this)

        storeDataStoreImport()
    }

    override fun writeFunction(function: KSFunctionDeclaration) {
        function.takeIf { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            ?.takeIf { declaration -> declaration.isAbstract }
            ?.takeIf { declaration -> declaration.parameters.size == 1 }
            ?.let { declaration ->
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

        property.takeIf { declaration -> declaration.isAbstract() }
            ?.takeIf { declaration -> declaration.type.toString() == "Flow" }
            ?.also { accumulator.storeImport("kotlinx.coroutines.flow.Flow") }
            ?.let {

                val returnType = it.type.resolve()

                val declaredProperty =
                    "    override val $it: ${getPropertyType(returnType)} = dataStore.data"

                accumulator.storeProperty(declaredProperty)
            }
    }

    override fun writeClass(declarationName: String) {

        val className = "${declarationName}Impl"

        accumulator.storeClass(
            "class $className(private val dataStore: DataStore<${accumulator.constructorArg}>) : $declarationName {"
        )
    }

    private fun storeDataStoreImport() {
        accumulator.storeImport("androidx.datastore.core.DataStore")
    }
}

private val KSTypeReference.isFunctionTypeWithBuilderArgument: Boolean
    get() = takeIf { it.resolve().isFunctionType }
        ?.element!!.typeArguments
        .map {
            it.type.toString()
        }.contains("Builder")