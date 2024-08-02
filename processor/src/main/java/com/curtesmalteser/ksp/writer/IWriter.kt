package com.curtesmalteser.ksp.writer

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

/**
 * Created by António Bastião on 02.11.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface IWriter {
    fun writeFunction(function: KSFunctionDeclaration)
    fun writeProperty(property: KSPropertyDeclaration)
    fun write()
}