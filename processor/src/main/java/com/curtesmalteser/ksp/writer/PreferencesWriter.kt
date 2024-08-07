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
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import java.io.OutputStream

/**
 * Created by António Bastião on 29.07.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class PreferencesWriter(
    output: OutputStream,
    private val declaration: KSClassDeclaration,
    logger: KSPLogger,
) : BaseWriter(output) {

    init {
        logger.info("PreferencesWriter init processing")

        declaration.containingFile?.accept(ClassVisitor(logger), this)

        storePreferencesImports()
    }

    override fun writeFunction(function: KSFunctionDeclaration) {
        function.takeIf { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            ?.takeIf { declaration -> declaration.isAbstract }
            ?.takeIf { declaration -> declaration.parameters.size == 1 }
            ?.let {
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

    override fun writeProperty(property: KSPropertyDeclaration) {

        property.takeIf { declaration -> declaration.isAbstract() }
            ?.takeIf { declaration -> declaration.type.toString() == "Flow" }
            ?.also {
                accumulator.storeImport("kotlinx.coroutines.flow.map")
                accumulator.storeImport("kotlinx.coroutines.flow.Flow")
            }?.let { it: KSPropertyDeclaration ->

                val returnType = it.type.resolve().toString()

                val declaredProperty = """    override val $it: $returnType = dataStore.data
                   |    .map { preferences ->
                   |        preferences[${
                    it.toString().replace("Flow", "Key")
                }] ?: ${it.getDefaultValues()}
                   |    }
                   """.trimMargin()

                accumulator.storeProperty(declaredProperty)
            }
    }

    override fun writeClass(declarationName: String) {

        val className = "${declarationName}Impl"

        accumulator.storeClass(
            "class $className(private val dataStore: DataStore<Preferences>) : $declarationName {"
        )
    }

    private fun storePreferencesImports() {
        accumulator.storeImport("androidx.datastore.preferences.core.edit")
        accumulator.storeImport("androidx.datastore.core.DataStore")
        accumulator.storeImport("androidx.datastore.preferences.core.Preferences")
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