package com.curtesmalteser.ksp.preferences.data

import com.curtesmalteser.ksp.annotation.WithPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Created by António Bastião on 02.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@WithPreferences
interface AppData {
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