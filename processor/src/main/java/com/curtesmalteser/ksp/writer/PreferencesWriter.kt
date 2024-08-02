package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.annotation.WithDefaultBoolean
import com.curtesmalteser.ksp.annotation.WithDefaultFloat
import com.curtesmalteser.ksp.annotation.WithDefaultInt
import com.curtesmalteser.ksp.annotation.WithDefaultLong
import com.curtesmalteser.ksp.annotation.WithDefaultString
import com.curtesmalteser.ksp.annotation.WithDefaultStringSet
import com.curtesmalteser.ksp.visitor.ClassVisitor
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isAnnotationPresent
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
    logger: KSPLogger,
) : IWriter {

    private val outputStreamWriter: OutputStreamWriter = OutputStreamWriter(output)
    private val accumulator: IAccumulator = Accumulator()

    init {
        logger.info("Preferences Writer init processing")

        declaration.containingFile?.accept(ClassVisitor(logger), Unit)

        writeProperty()
        writeFunction()
    }

    override fun writeFunction() {
        declaration.getAllFunctions()
            .filter { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            .filter { declaration -> declaration.isAbstract }
            .filter { declaration -> declaration.parameters.size == 1 }.forEach {
                it.parameters.first().let { parameter ->

                    parameter.generateKey()

                    val paramType = if (parameter.type.toString() == "Set") {

                        val genericType = parameter.type.resolve()
                            .innerArguments.first().type.toString()

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

        declaration.getAllProperties().filter { declaration -> declaration.isAbstract() }
            .filter { declaration ->
                declaration.type.toString() == "Flow"
            }.also {
                accumulator.storeImport("kotlinx.coroutines.flow.map")
                accumulator.storeImport("kotlinx.coroutines.flow.Flow")
            }.forEach {

                val returnType = it.type.resolve().toString()

                val property = """    override val $it: $returnType = dataStore.data
                   |    .map { preferences ->
                   |        preferences[${
                    it.toString().replace("Flow", "Key")
                }] ?: ${it.getDefaultValues()}
                   |    }
                   """.trimMargin()

                accumulator.storeProperty(property)
            }
    }

    override fun write() {

        accumulator.storeImport("androidx.datastore.preferences.core.edit")

        outputStreamWriter.write("package ${declaration.packageName.asString()}")
        outputStreamWriter.appendLine().appendLine()
        accumulator.storeImport("androidx.datastore.core.DataStore")
        accumulator.storeImport("androidx.datastore.preferences.core.Preferences")
        outputStreamWriter.appendLine()
        accumulator.importSet.forEach {
            outputStreamWriter.write(it)
            outputStreamWriter.appendLine()
        }

        outputStreamWriter.appendLine().appendLine()

        val fileName = declaration.simpleName.asString()
        val className = "${fileName}Impl"

        outputStreamWriter.write("class $className(private val dataStore: DataStore<Preferences>) : $fileName {\n")

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

    private fun KSPropertyDeclaration.getDefaultValues(): String =
        when (type.resolve().toString()) {
            "Flow<Boolean>" -> getDefaultBoolean().toString()
            "Flow<Int>" -> getDefaultInt().toString()
            "Flow<Long>" -> "${getDefaultLong()}L"
            "Flow<Float>" -> "${getDefaultFloat()}F"
            "Flow<String>" -> getDefaultString()
            "Flow<Set<String>>" -> getDefaultStringSet()
            else -> throw UnsupportedOperationException("Type not supported: $type")
        }

    @OptIn(KspExperimental::class)
    private fun KSPropertyDeclaration.getDefaultBoolean(): Boolean = takeIf {
        it.isAnnotationPresent(WithDefaultBoolean::class)
    }?.getAnnotationsByType(WithDefaultBoolean::class)?.first()?.value ?: false

    @OptIn(KspExperimental::class)
    private fun KSPropertyDeclaration.getDefaultInt(): Int = takeIf {
        it.isAnnotationPresent(WithDefaultInt::class)
    }?.getAnnotationsByType(WithDefaultInt::class)?.first()?.value ?: 0

    @OptIn(KspExperimental::class)
    private fun KSPropertyDeclaration.getDefaultLong(): Long = takeIf {
        it.isAnnotationPresent(WithDefaultLong::class)
    }?.getAnnotationsByType(WithDefaultLong::class)?.first()?.value ?: 0L

    @OptIn(KspExperimental::class)
    private fun KSPropertyDeclaration.getDefaultFloat(): Float = takeIf {
        it.isAnnotationPresent(WithDefaultFloat::class)
    }?.getAnnotationsByType(WithDefaultFloat::class)?.first()?.value ?: 0F

    @OptIn(KspExperimental::class)
    private fun KSPropertyDeclaration.getDefaultString(): String = takeIf {
        it.isAnnotationPresent(WithDefaultString::class)
    }?.getAnnotationsByType(WithDefaultString::class)?.first()
        ?.value
        ?.let { "\"$it\"" } ?: "\"\""

    @OptIn(KspExperimental::class)
    private fun KSPropertyDeclaration.getDefaultStringSet(): String = takeIf {
        it.isAnnotationPresent(WithDefaultStringSet::class)
    }?.getAnnotationsByType(WithDefaultStringSet::class)
        ?.first()?.value
        ?.toSet()
        ?.joinToString(
            separator = ",\n",
            prefix = "\n",
            postfix = "\n"
        ) { "                \"$it\"" }
        ?.let { "setOf($it            )" }
        ?: "emptySet()"
}