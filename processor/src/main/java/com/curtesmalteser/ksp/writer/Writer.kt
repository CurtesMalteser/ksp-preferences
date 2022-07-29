package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.processor.ClassVisitor
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
    /**
     * Key for values stored in Preferences. Type T is the type of the value associated with the
     * Key.
     *
     * T must be one of the following: Boolean, Int, Long, Float, String, Set<String>.
     *
     * Construct Keys for your data type using: [booleanPreferencesKey], [intPreferencesKey],
     * [longPreferencesKey], [floatPreferencesKey], [stringPreferencesKey], [stringSetPreferencesKey]
     */
    fun writeFunction(
        classDeclaration: KSClassDeclaration,
    ) {

        val symbolName = classDeclaration.simpleName.asString().lowercase()

        accumulator.storeProperty("    val $symbolName = true\n")

        classDeclaration.getAllFunctions()
            .filter { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            .filter { declaration -> declaration.isAbstract }
            .filter { declaration -> declaration.parameters.size == 1 }
            .forEach {
                it.parameters.first().let { parameter ->
                    parameter.generateKey()

                    accumulator.storeFunction(
                        """    override suspend fun ${parameter.parent}(${parameter}: ${parameter.type}){
                            |       TODO("Not yet implemented")
                            |    }
                        """.trimMargin()
                    )

                }
            }

    }

    fun write() {

        val visitor = ClassVisitor(logger, this)

        declaration.containingFile?.accept(visitor, writer)

        writer.write("package ${declaration.packageName.asString()}")
        writer.appendLine().appendLine()
        writer.write("import android.content.Context")
            logger.warn("Test: before import")
            writer.appendLine()
        accumulator.importSet.forEach {
            logger.warn("Test: $it")
            writer.write(it)
        }

        writer.appendLine().appendLine()

        val fileName = declaration.simpleName.asString()
        val className = "${fileName}Impl"

        writer.write("class $className(context: Context) : $fileName {\n")

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

    private fun KSValueParameter.generateKey() {
        when {
            type.toString() == "Boolean" -> {

                logger.warn("test: $type: parameter: $this")
            }
            type.toString() == "Int" -> {
                accumulator.storeImport("import androidx.datastore.preferences.core.intPreferencesKey")
                accumulator.storeProperty("    val EXAMPLE_COUNTER = intPreferencesKey(\"example_counter\")")
                logger.warn("test: $type: parameter: ${accumulator.importSet}")
            }
            type.toString() == "Long" -> {

                logger.warn("test: $type: parameter: $this")
            }
            type.toString() == "Float" -> {

                logger.warn("test: $type: parameter: $this")
            }
            type.toString() == "String" -> {

                logger.warn("test: $type: parameter: $this")
            }
            type.toString() == "Set<String>" -> {

                logger.warn("test: $type: parameter: $this")
            }
        }
    }

}