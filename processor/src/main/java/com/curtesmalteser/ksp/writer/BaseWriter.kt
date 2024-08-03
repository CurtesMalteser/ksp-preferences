package com.curtesmalteser.ksp.writer

import com.curtesmalteser.ksp.visitor.ClassVisitor
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Created by António Bastião on 03.08.2024
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
abstract class BaseWriter(
    output: OutputStream,
    declaration: KSClassDeclaration,
    logger: KSPLogger,
) : IWriter {

    protected val accumulator: IAccumulator = Accumulator()
    protected val outputStreamWriter: OutputStreamWriter = OutputStreamWriter(output)

    init {
        logger.info("Preferences Writer init processing")

        declaration.containingFile?.accept(ClassVisitor(logger), this)
    }

    override fun writePackage(packageName: String) {
        outputStreamWriter.write("package $packageName")
        outputStreamWriter.appendLine()
    }

}