package com.curtesmalteser.ksp.writer

interface IAccumulator {
    val importSet: Set<String>
    val propertySet: Set<String>
    val functionSet: Set<String>
    fun storeImport(import: String): Boolean
    fun storeProperty(declaration: String): Boolean
    fun storeFunction(declaration: String): Boolean
}

/**
 * Created by António Bastião on 29.07.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class Accumulator : IAccumulator {

    private val _importSet: MutableSet<String> = mutableSetOf()
    override val importSet: Set<String> = _importSet

    private val _propertySet: MutableSet<String> = mutableSetOf()
    override val propertySet: Set<String> = _propertySet

    private val _functionSet: MutableSet<String> = mutableSetOf()
    override val functionSet: Set<String> = _functionSet

    override fun storeImport(import: String) = _importSet.add("$IMPORT $import")

    override fun storeProperty(declaration: String) = _propertySet.add(declaration)

    override fun storeFunction(declaration: String) = _functionSet.add(declaration)

    private companion object {
        const val IMPORT = "import"
    }
}