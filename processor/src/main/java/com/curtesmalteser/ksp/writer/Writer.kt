package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.processor.ClassVisitor
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Created by António Bastião on 29.07.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class Writer(
    output: OutputStream,
    private val declaration: KSClassDeclaration,
    private val logger: KSPLogger,
    private val accumulator: IAccumulator,
) {

    private val writer: OutputStreamWriter

    init {
        writer = OutputStreamWriter(output)
    }

    fun writeFunction(
        classDeclaration: KSClassDeclaration,
    ) {

        classDeclaration.getAllFunctions()
            .filter { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            .filter { declaration -> declaration.isAbstract }
            .filter { declaration -> declaration.parameters.size == 1 }
            .forEach {
                it.parameters.first().let { parameter ->

                    parameter.generateKey()

                    val paramType = if (parameter.type.toString() == "Set") {

                        val genericType = parameter.type
                            .resolve()
                            .innerArguments
                            .first()
                            .type
                            .toString()

                        if (genericType == "String") {
                            "Set<String>"
                        } else throw Exception("Type not supported: Set<$genericType>")
                    } else {
                        parameter.type.toString()
                    }

                    accumulator.storeFunction(
                        """    override suspend fun ${parameter.parent}(${parameter}: ${paramType}){
                            |        context.dataStore.edit {
                            |            it[${parameter}Key] = $parameter
                            |        }
                            |    }
                        """.trimMargin()
                    )

                }
            }

    }

    fun write() {

        val visitor = ClassVisitor(logger, this)

        declaration.containingFile?.accept(visitor, writer)

        accumulator.storeImport("import androidx.datastore.preferences.core.edit")


        writer.write("package ${declaration.packageName.asString()}")
        writer.appendLine().appendLine()
        writer.write("import android.content.Context")
        writer.appendLine()
        accumulator.importSet.forEach {
            writer.write(it)
            writer.appendLine()
        }

        writer.appendLine().appendLine()

        val fileName = declaration.simpleName.asString()
        val className = "${fileName}Impl"

        writer.write("class $className(private val context: Context) : $fileName {\n")

        writer.appendLine().appendLine()

        accumulator.propertySet.forEach {
            logger.warn(it)
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

    private fun KSValueParameter.generateKey() {

        val storeProperty = { type: String ->
            val preferencesKey = "${type.replaceFirstChar { it.lowercase() }}PreferencesKey"
            accumulator.storeImport("import androidx.datastore.preferences.core.$preferencesKey")
            accumulator.storeProperty("    private val ${this}Key = $preferencesKey(\"$this\")")
        }

        when (val typeString = type.toString()) {
            "Boolean" -> storeProperty(typeString)
            "Int" -> storeProperty(typeString)
            "Long" -> storeProperty(typeString)
            "Float" -> storeProperty(typeString)
            "String" -> storeProperty(typeString)
            "Set" -> storeProperty("stringSet")
        }
    }

}