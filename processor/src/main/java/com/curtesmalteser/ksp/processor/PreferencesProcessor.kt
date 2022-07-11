package com.curtesmalteser.ksp.processor

import com.curtesmalteser.ksp.annotation.WithPreferences
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

            logger.warn(name)

            val allFiles = resolver.getSymbolsWithAnnotation(name)
                .filterIsInstance<KSClassDeclaration>()

            allFiles.toList().map { declaration ->
                val fileName = declaration.simpleName.asString()
                val className = "${fileName}Impl"
                codeGenerator.createNewFile(Dependencies(false), "", className, "kt")
                    .use { output ->
                        OutputStreamWriter(output).use { writer ->
                            writer.write("package ${declaration.packageName.asString()}")
                            writer.appendLine().appendLine()
                            writer.write("import android.content.Context")
                            writer.appendLine().appendLine()
                            writer.write("class $className(context: Context) : $fileName {\n")

                            val visitor = ClassVisitor(logger)
                            declaration.containingFile?.accept(visitor, writer)

                            writer.write("}")
                            writer.close()
                        }
                    }

            }
        }

        return emptyList()
    }
}

class ClassVisitor(private val logger: KSPLogger) : KSTopDownVisitor<OutputStreamWriter, Unit>() {

    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) = Unit

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)

        logger.logging("Visiting declaration of: ${classDeclaration.simpleName.getShortName()}")

        classDeclaration.let {
            it.annotations.firstOrNull { annotation ->
                annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithPreferences::class.qualifiedName
            }?.run { it }
        }?.let {
            if (it.classKind == ClassKind.INTERFACE) {
                val symbolName = classDeclaration.simpleName.asString().lowercase()

                data.apply {
                    appendLine()
                    write("    val $symbolName = true\n")
                    appendLine()
                }

                writeFunction(classDeclaration, data)
            }
        }

    }

    private fun writeFunction(
        classDeclaration: KSClassDeclaration,
        data: OutputStreamWriter
    ) {
        classDeclaration.getAllFunctions()
            .filter { declaration -> declaration.modifiers.contains(Modifier.SUSPEND) }
            .filter { declaration -> declaration.isAbstract }
            .filter { declaration -> declaration.parameters.size == 1 }
            .forEach {
                it.parameters.first().let { parameter ->
                    data.write(
                        """    override suspend fun ${parameter.parent}(${parameter}: ${parameter.type}){
                            |       TODO("Not yet implemented")
                            |    }
                        """.trimMargin()
                    )
                    data.appendLine().appendLine()
                }

            }
    }
}