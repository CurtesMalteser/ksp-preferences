package com.curtesmalteser.ksp.processor

import com.curtesmalteser.ksp.annotation.WithPreferences
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
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

                            val visitor = ClassVisitor()
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

class ClassVisitor : KSTopDownVisitor<OutputStreamWriter, Unit>() {
    override fun defaultHandler(node: KSNode, data: OutputStreamWriter) {
    }

    override fun visitClassDeclaration(
        classDeclaration: KSClassDeclaration,
        data: OutputStreamWriter
    ) {
        super.visitClassDeclaration(classDeclaration, data)
        classDeclaration.let {
            it.annotations.firstOrNull { annotation ->
                annotation.annotationType.resolve().declaration.qualifiedName?.asString() == WithPreferences::class.qualifiedName
            }?.run { it }
        }?.let {
            if (it.classKind == ClassKind.INTERFACE) {
                val symbolName = classDeclaration.simpleName.asString().lowercase()
                data.write("    val $symbolName = true\n")
            }
        }

    }
}