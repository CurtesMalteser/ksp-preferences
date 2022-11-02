package com.curtesmalteser.ksp.writer

import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * Created by António Bastião on 02.11.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface IWriter {
    fun writeFunction(classDeclaration: KSClassDeclaration)
    fun writeProperty(classDeclaration: KSClassDeclaration)
    fun write()
}