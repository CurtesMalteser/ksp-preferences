package com.curtesmalteser.ksp.preferences.data

import kotlinx.coroutines.flow.Flow

/**
 * Created by António Bastião on 09.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface MainRepository {

    val myBooleanFlow: Flow<Boolean>
    val myIntFlow: Flow<Int>
    val myLongFlow: Flow<Long>
    val myFloatFlow: Flow<Float>
    val myStringFlow: Flow<String>
    val myStringSetFlow: Flow<Set<String>>

    suspend fun testBoolean(myBoolean: Boolean)
    suspend fun testInt(myInt: Int)
    suspend fun testLong(myLong: Long)
    suspend fun testFloat(myFloat: Float)
    suspend fun testString(myString: String)
    suspend fun testStringSet(myStringSet: Set<String>)
}

class MainRepositoryImpl(
    private val prefs: AppData,
) : MainRepository {

    override val myBooleanFlow: Flow<Boolean>
        get() = prefs.myBooleanFlow

    override val myIntFlow: Flow<Int>
        get() = prefs.myIntFlow

    override val myLongFlow: Flow<Long>
        get() = prefs.myLongFlow

    override val myFloatFlow: Flow<Float>
        get() = prefs.myFloatFlow

    override val myStringFlow: Flow<String>
        get() = prefs.myStringFlow

    override val myStringSetFlow: Flow<Set<String>>
        get() = prefs.myStringSetFlow

    override suspend fun testBoolean(myBoolean: Boolean) = prefs.testBoolean(myBoolean)

    override suspend fun testInt(myInt: Int) = prefs.testInt(myInt)

    override suspend fun testLong(myLong: Long) = prefs.testLong(myLong)

    override suspend fun testFloat(myFloat: Float) = prefs.testFloat(myFloat)

    override suspend fun testString(myString: String) = prefs.testString(myString)

    override suspend fun testStringSet(myStringSet: Set<String>) = prefs.testStringSet(myStringSet)
}


