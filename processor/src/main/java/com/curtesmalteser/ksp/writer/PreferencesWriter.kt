package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.visitor.ClassVisitor
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Created by António Bastião on 29.07.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class PreferencesWriter(
    output: OutputStream,
    private val declaration: KSClassDeclaration,
    private val logger: KSPLogger,
    private val accumulator: IAccumulator,
) : IWriter {

    private val writer: OutputStreamWriter

    init {
        logger.warn("Preferences Writer init processing")
        writer = OutputStreamWriter(output)
    }

    override fun writeFunction() {

        declaration.getAllFunctions()
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
                            |        dataStore.edit {
                            |            it[${parameter}Key] = $parameter
                            |        }
                            |    }
                        """.trimMargin()
                    )

                }
            }

    }

    override fun writeProperty() {

        declaration.getAllProperties()
            .filter { declaration -> declaration.isAbstract() }
            .filter { declaration ->
                declaration.type.toString() == "Flow"
            }
            .also {
                accumulator.storeImport("kotlinx.coroutines.flow.map")
                accumulator.storeImport("kotlinx.coroutines.flow.Flow")
            }
            .forEach {

                val returnType = it.type
                    .resolve().toString()

               val property =  """    override val $it: $returnType = dataStore.data
                   |    .map { preferences ->
                   |        preferences[${it.toString().replace("Flow", "Key")}] ?: ${it.getDefaultValues()}
                   |    }
                   """.trimMargin()

                accumulator.storeProperty(property)
            }
    }

    override fun write() {

        val visitor = ClassVisitor(logger, this)

        declaration.containingFile?.accept(visitor, writer)

        accumulator.storeImport("androidx.datastore.preferences.core.edit")

        writer.write("package ${declaration.packageName.asString()}")
        writer.appendLine().appendLine()
        accumulator.storeImport("androidx.datastore.core.DataStore")
        accumulator.storeImport("androidx.datastore.preferences.core.Preferences")
        writer.appendLine()
        accumulator.importSet.forEach {
            writer.write(it)
            writer.appendLine()
        }

        writer.appendLine().appendLine()

        val fileName = declaration.simpleName.asString()
        val className = "${fileName}Impl"

        writer.write("class $className(private val dataStore: DataStore<Preferences>) : $fileName {\n")

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

        val storeProperty = { type: String ->
            val preferencesKey = "${type.replaceFirstChar { it.lowercase() }}PreferencesKey"
            accumulator.storeImport("androidx.datastore.preferences.core.$preferencesKey")
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

    private fun KSPropertyDeclaration.getDefaultValues() : String{
       return when (type.resolve().toString()) {
            "Flow<Boolean>" -> "false"
            "Flow<Int>" -> "0"
            "Flow<Long>" -> "0L"
            "Flow<Float>" -> "0F"
            "Flow<String>" -> "\"\""
            "Flow<Set<String>>" -> "emptySet()"
           else -> throw UnsupportedOperationException("Type not supported: $type")
       }
    }

}