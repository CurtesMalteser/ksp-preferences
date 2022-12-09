package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.processor.ClassVisitor
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
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

                    val importType = parameter.type.takeIf { it.resolve().isFunctionType }?.let {
                        it.element!!.typeArguments.first().type
                    }

                    val paramType = parameter.type

                    val argImport = paramType.let {
                        val argImport = parameter.containingFile?.packageName?.getQualifier()
                        argImport!! + "." + importType
                    }

                    accumulator.storeImport(argImport)

                    accumulator.storeFunction(
                        """    override suspend fun ${parameter.parent}(${parameter}: ${paramType}){
                        |        dataStore.updateData {
                        |            ${parameter}(it)
                        |        }
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
        writer.close()
    }
}