package com.curtesmalteser.ksp.processor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.writer.Accumulator
import com.curtesmalteser.ksp.writer.ProtoDataStoreWriter
import com.curtesmalteser.ksp.writer.Writer
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * Created by António Bastião on 26.05.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class PreferencesProcessor(val codeGenerator: CodeGenerator, val logger: KSPLogger) :
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
        logger.warn(name)

        val allFiles =
            resolver.getSymbolsWithAnnotation(name).filterIsInstance<KSClassDeclaration>()

        allFiles.toList().map { declaration ->
            val packageName = declaration.containingFile!!.packageName.asString()
            val fileName = declaration.simpleName.asString()
            val className = "${fileName}Impl"
            codeGenerator.createNewFile(
                Dependencies(false, declaration.containingFile!!), packageName, className, "kt"
            ).use { output ->
                when {
                    T::class == WithPreferences::class -> Writer(
                        output, declaration, logger, Accumulator()
                    ).write()
                    T::class == WithProto::class -> ProtoDataStoreWriter(
                        output, declaration, logger, Accumulator()
                    ).write()
                }
            }
        }
    }
}