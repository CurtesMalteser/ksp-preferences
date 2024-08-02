package com.curtesmalteser.ksp.visitor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
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

        getDeclarationToAnnotationName(classDeclaration)
            ?.let { (declaration, annotationName) ->
                if (declaration.classKind == ClassKind.INTERFACE) {
                    logger.info("Annotation found: ${declaration.simpleName.getShortName()}")
                } else {
                    throw InvalidAnnotationTargetException(annotationName = annotationName)
                }
            }

        classDeclaration.getDeclaredFunctions().forEach { it.accept(this, Unit) }
        classDeclaration.getDeclaredProperties().forEach { it.accept(this, Unit) }
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        logger.info("Visiting function declaration: ${function.simpleName.getShortName()}")
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        logger.info("Visiting property declaration: ${property.simpleName.getShortName()}")
    }

    private fun getDeclarationToAnnotationName(declaration: KSClassDeclaration): Pair<KSClassDeclaration, String>? =
        run {
            declaration.annotations.firstOrNull(::isValidAnnotation)?.let {
                declaration to extractQualifiedName(it)
            }
        }

    private fun isValidAnnotation(annotation: KSAnnotation): Boolean {
        return when (annotation.name) {
            WithProto::class.qualifiedName -> true
            WithPreferences::class.qualifiedName -> true
            else -> false
        }
    }

    private fun extractQualifiedName(annotation: KSAnnotation): String {
        return when (annotation.name) {
            WithProto::class.qualifiedName -> WithProto::class.qualifiedName!!
            WithPreferences::class.qualifiedName -> WithPreferences::class.qualifiedName!!
            else -> throw AnnotationNotAllowedException(annotationName = annotation.name)
        }
    }

    private val KSAnnotation.name: String
        get() = annotationType.resolve().declaration.qualifiedName?.asString()!!

    class InvalidAnnotationTargetException(annotationName: String) :
        Exception("Annotation: $annotationName can only be applied to an interface")

    class AnnotationNotAllowedException(annotationName: String) :
        Exception("Annotation: $annotationName Not Allowed")
}