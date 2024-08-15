package com.curtesmalteser.ksp.writer

/**
 * Created by António Bastião on 29.07.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
class Accumulator {

    // TODO: Verify use contracts when throwing exception for not null or empty string
    //  Leaving as nullable for now
    private var _classDeclaration: String? = null
    val classDeclaration: String
        get() = _classDeclaration ?: throw IllegalStateException("Class declaration not set")

    private var _constructorArg: String? =null
    var constructorArg: String
        get() = _constructorArg!!
        set(value) {
            _constructorArg = value
        }

    private val _importSet: MutableSet<String> = mutableSetOf()
    val importSet: Set<String> = _importSet

    private val _propertySet: MutableSet<String> = mutableSetOf()
    val propertySet: Set<String> = _propertySet

    private val _functionSet: MutableSet<String> = mutableSetOf()
    val functionSet: Set<String> = _functionSet

    fun storeImport(import: String) = _importSet.add("$IMPORT $import")

    fun storeProperty(declaration: String) = _propertySet.add(declaration)

    fun storeFunction(declaration: String) = _functionSet.add(declaration)

    // TODO: Make sure that is not called twice, check if it's not null before assign
    //  and throw exception if it is already set
    fun storeClass(declaration: String) {
        _classDeclaration = declaration
    }

    private companion object {
        const val IMPORT = "import"
    }
}