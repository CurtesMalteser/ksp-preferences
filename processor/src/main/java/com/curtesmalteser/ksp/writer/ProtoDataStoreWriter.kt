package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.processor.ClassVisitor
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import java.io.OutputStream
import java.io.OutputStreamWriter
import kotlin.reflect.cast

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
        logger.warn("ProtoDataStoreWriter init processing")
        writer = OutputStreamWriter(output)
    }

    override fun writeFunction(classDeclaration: KSClassDeclaration) {
        classDeclaration.getAllFunctions()
            .filter { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            .filter { declaration -> declaration.isAbstract }
            .filter { declaration -> declaration.parameters.size == 1 }.forEach {
                it.parameters.first().let { parameter ->

                    logger.warn("paramType ${classDeclaration.annotations.single { annotation ->
                        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithProto::class.qualifiedName
                    }}")

                    val paramType = parameter.type.toString()

                    val paramImport =  classDeclaration.annotations.single { annotation ->
                        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithProto::class.qualifiedName
                    }.let {


                        val genericType = it.annotationType
                            .resolve()
                            //.innerArguments
                            //.first()
                            //.type
                            //.toString()

                        val paramImport = it.annotationType.element?.typeArguments?.single()!!.type?.containingFile?.packageName?.getQualifier()
                        logger.warn("arg: $paramImport")
                        paramImport!! + "." + paramType
                    }

                    accumulator.storeImport(paramImport)


                    accumulator.storeFunction(
                        """    override suspend fun ${parameter.parent}(${parameter}: ${paramType}){
                            |           TODO("Not yet implemented")
                            |    }
                        """.trimMargin()
                    )

                }
            }
    }

    override fun writeProperty(classDeclaration: KSClassDeclaration) {
        logger.warn("writeProperty not implemented")
    }

    override fun write() {
        val visitor = ClassVisitor(logger, this)

        declaration.containingFile?.accept(visitor, writer)

        //accumulator.storeImport("androidx.datastore.preferences.core.edit")


        writer.write("package ${declaration.packageName.asString()}")
        writer.appendLine().appendLine()
        accumulator.storeImport("androidx.datastore.core.DataStore")
        //accumulator.storeImport("androidx.datastore.preferences.core.Preferences")
        writer.appendLine()
        accumulator.importSet.forEach {
            writer.write(it)
            writer.appendLine()
        }

        writer.appendLine().appendLine()

        val fileName = declaration.simpleName.asString()
        val className = "${fileName}Impl"

        writer.write("class $className(private val dataStore: DataStore<UserPreferences>) : $fileName {\n")

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
        writer.close()    }
}