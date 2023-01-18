package com.curtesmalteser.ksp.processor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.writer.Accumulator
import com.curtesmalteser.ksp.writer.IWriter
import com.curtesmalteser.ksp.writer.ProtoDataStoreWriter
import com.curtesmalteser.ksp.writer.Writer
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import java.io.OutputStreamWriter

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

class ClassVisitor(private val logger: KSPLogger, private val writer: IWriter) :
    KSTopDownVisitor<OutputStreamWriter, Unit>() {

    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) = Unit

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration, data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)

        logger.logging("Visiting declaration of: ${classDeclaration.simpleName.getShortName()}")

        classDeclaration.let {
            it.annotations.firstOrNull { annotation ->
                isWithPreferences(annotation) || isWithProto(annotation)
            }?.run { it }
        }?.let {
            if (it.classKind == ClassKind.INTERFACE) {
                writer.writeFunction(classDeclaration)
                writer.writeProperty(classDeclaration)
            }
        }

    }

    private fun isWithProto(annotation: KSAnnotation) =
        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithProto::class.qualifiedName

    private fun isWithPreferences(annotation: KSAnnotation) =
        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithPreferences::class.qualifiedName

}