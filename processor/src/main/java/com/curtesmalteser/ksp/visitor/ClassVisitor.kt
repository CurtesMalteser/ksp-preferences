package com.curtesmalteser.ksp.visitor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.writer.IWriter
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.visitor.KSTopDownVisitor

/**
 * Created by António Bastião on 04.02.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class ClassVisitor(private val logger: KSPLogger) : KSTopDownVisitor<IWriter, Unit>() {

    override fun visitFile(file: KSFile, data: IWriter) {
        logger.info("Visiting file: ${file.packageName.asString()}")
        data.writePackage(file.packageName.asString())
        file.declarations.forEach { it.accept(this, data) }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: IWriter) {

        val className = classDeclaration.simpleName.asString()

        logger.info("Visiting class declaration of: $className")

        getDeclarationToAnnotationName(classDeclaration)
            ?.let { (declaration, annotationName) ->
                if (declaration.classKind == ClassKind.INTERFACE) {
                    logger.info("Annotation found: ${declaration.simpleName.getShortName()}")
                } else {
                    throw InvalidAnnotationTargetException(annotationName = annotationName)
                }
            }

        classDeclaration.getDeclaredProperties().forEach { it.accept(this, data) }
        classDeclaration.getDeclaredFunctions().forEach { it.accept(this, data) }
        writeClassAfterDeclarations(data, classDeclaration.simpleName.asString())
    }

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: IWriter) {
        logger.info("Visiting function declaration: ${function.simpleName.getShortName()}")
        data.writeFunction(function)
    }

    override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: IWriter) {
        logger.info("Visiting property declaration: ${property.simpleName.getShortName()}")
        data.writeProperty(property)
    }

    override fun defaultHandler(node: KSNode, data: IWriter) = Unit

    // Functions and properties must be processed first to generate the class constructor arguments.
    private fun writeClassAfterDeclarations(data: IWriter, declarationName: String) {
        data.writeClass(declarationName)
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