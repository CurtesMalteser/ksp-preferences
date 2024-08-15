package com.curtesmalteser.ksp.test.processor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.test.writer.FakePreferencesWriter
import com.curtesmalteser.ksp.test.writer.FakeProtoDataStoreWriter
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class TestPreferencesProcessor(val codeGenerator: CodeGenerator, val logger: KSPLogger) :
    SymbolProcessor {
    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {

        if (invoked) {
            return emptyList()
        }

        invoked = true

        WithPreferences::class.qualifiedName?.let { name ->
            generateClassesForAnnotation<WithPreferences>(name, resolver)
        }

        WithProto::class.qualifiedName?.let { name ->
            generateClassesForAnnotation<WithProto>(name, resolver)
        }

        return emptyList()
    }

    private inline fun <reified T : Annotation> generateClassesForAnnotation(
        name: String, resolver: Resolver
    ) {

        val allFiles =
            resolver.getSymbolsWithAnnotation(name).filterIsInstance<KSClassDeclaration>()

        allFiles.toList().map { declaration ->
            val packageName = declaration.containingFile!!.packageName.asString()
            val declarationFileName = declaration.simpleName.asString()
            val fileName = "${declarationFileName}Mock"

            codeGenerator.createNewFile(
                Dependencies(false, declaration.containingFile!!), packageName, fileName, "kt"
            ).use { output ->
                when {
                    T::class == WithPreferences::class -> FakePreferencesWriter(
                        output, declaration, logger
                    ).write()

                    T::class == WithProto::class -> FakeProtoDataStoreWriter(
                        output, declaration, logger
                    ).write()
                }
            }
        }
    }

}