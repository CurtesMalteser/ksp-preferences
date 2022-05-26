package com.curtesmalteser.ksp.processor

import com.curtesmalteser.ksp.annotation.Preferences
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import java.io.OutputStreamWriter
import com.google.devtools.ksp.processing.CodeGenerator

/**
 * Created by António Bastião on 26.05.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class PreferencesProcessor(val codeGenerator: CodeGenerator, val logger: KSPLogger) : SymbolProcessor {

    private var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {

        if (invoked) {
            return emptyList()
        }
        invoked = true

        Preferences::class.qualifiedName?.let { name ->

            logger.warn(name)

            val allFiles = resolver.getSymbolsWithAnnotation(name)
                .filterIsInstance<KSClassDeclaration>()

            allFiles.toList().first().let { declaration ->
                val fileName = declaration.simpleName.asString()
                val className = "${fileName}Impl"
                    codeGenerator.createNewFile(Dependencies(false), "", className, "kt").use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write("package ${declaration.packageName.asString()}\n\n")
                        writer.write("class $className : $fileName {\n")

                        val visitor = ClassVisitor()
                        resolver.getAllFiles().forEach { file ->
                            file.accept(visitor, writer)
                        }

                        writer.write("}\n")
                        writer.close()
                    }
                }

            }
        }

        return emptyList()
    }
}

class ClassVisitor : KSTopDownVisitor<OutputStreamWriter, Unit>() {
    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) {
    }

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)
        val symbolName = classDeclaration.simpleName.asString().lowercase()
        data.write("    val $symbolName = true\n")
    }
}