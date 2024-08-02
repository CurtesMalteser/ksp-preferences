package com.curtesmalteser.ksp.visitor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.writer.IWriter
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import java.io.OutputStreamWriter

/**
 * Created by António Bastião on 04.02.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class ClassVisitor(private val logger: KSPLogger, private val writer: IWriter) :
    KSTopDownVisitor<OutputStreamWriter, Unit>() {

    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) = Unit

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration, data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)

        logger.logging("Visiting declaration of: ${classDeclaration.simpleName.getShortName()}")

        classDeclaration.let { declaration ->
            declaration.annotations.firstOrNull { annotation ->
                isWithPreferences(annotation) || isWithProto(annotation)
            }?.run { declaration }
        }?.let {
            if (it.classKind == ClassKind.INTERFACE) {
                writer.writeFunction()
                writer.writeProperty()
            }
        }

    }

    private fun isWithProto(annotation: KSAnnotation) =
        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithProto::class.qualifiedName

    private fun isWithPreferences(annotation: KSAnnotation) =
        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithPreferences::class.qualifiedName

}