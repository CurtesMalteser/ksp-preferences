package com.curtesmalteser.ksp.visitor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

/**
 * Created by António Bastião on 04.02.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class ClassVisitor(private val logger: KSPLogger) :
    KSVisitorVoid() {

    override fun visitFile(file: KSFile, data: Unit) {
        logger.info("Visiting file: ${file.fileName}")
        file.declarations.forEach { it.accept(this, Unit) }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        val className = classDeclaration.simpleName.getShortName()

        logger.info("Visiting class declaration of: $className")

        classDeclaration.let { declaration ->
            declaration.annotations.firstOrNull { annotation ->
                // todo: return annotation name or null, so the code isn't executed
                //  make generic function to check if annotation is correct and return annotation name
                //  the return string join to declaration and concatenate with
                //  InvalidAnnotationTargetException message
                isWithPreferences(annotation) || isWithProto(annotation)
            }?.run { declaration }
        }?.let {
            if (it.classKind == ClassKind.INTERFACE) {
                logger.info("Annotation found: ${it.simpleName.getShortName()}")
            } else {
                throw InvalidAnnotationTargetException("Annotation can only be applied to an interface")
            }
        }

        classDeclaration.getDeclaredFunctions().forEach { it.accept(this, Unit) }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        logger.info("Visiting function declaration: ${function.simpleName.getShortName()}")
    }

    private fun isWithProto(annotation: KSAnnotation) =
        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithProto::class.qualifiedName

    private fun isWithPreferences(annotation: KSAnnotation) =
        annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithPreferences::class.qualifiedName

    class InvalidAnnotationTargetException(message: String) : Exception(message)
}