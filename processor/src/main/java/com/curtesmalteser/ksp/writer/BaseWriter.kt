package com.curtesmalteser.ksp.writer

import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Created by António Bastião on 03.08.2024
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
abstract class BaseWriter(output: OutputStream) : IWriter {

    protected val accumulator = Accumulator()
    private val outputStreamWriter: OutputStreamWriter = OutputStreamWriter(output)

    override fun writePackage(packageName: String) {
        outputStreamWriter.write("package $packageName")
        outputStreamWriter.appendLine().appendLine()
    }

    override fun write() {
        writeImports()
        outputStreamWriter.appendLine()
        writeClassDeclaration()
        outputStreamWriter.appendLine()
        writeProperties()
        writeFunctions()
        writeCloseClassDeclaration()
        outputStreamWriter.close()
    }

    private fun writeImports() {
        accumulator.importSet.forEach { import ->
            outputStreamWriter.run {
                write(import)
                appendLine()
            }
        }
    }

    private fun writeClassDeclaration() {
        outputStreamWriter.run {
            write(accumulator.classDeclaration)
            appendLine()
        }
    }

    private fun writeProperties() {
        accumulator.propertySet.forEach { property ->
            outputStreamWriter.run {
                write(property)
                appendLine().appendLine()
            }
        }
    }

    private fun writeFunctions() {
        accumulator.functionSet.forEach { function ->
            outputStreamWriter.run {
                write(function)
                appendLine().appendLine()
            }
        }
    }

    private fun writeCloseClassDeclaration() {
        outputStreamWriter.write("}")
    }
}
