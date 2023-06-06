package com.curtesmalteser.ksp.preferences.data

import com.curtesmalteser.ksp.annotation.WithDefaultBoolean
import com.curtesmalteser.ksp.annotation.WithDefaultFloat
import com.curtesmalteser.ksp.annotation.WithDefaultInt
import com.curtesmalteser.ksp.annotation.WithDefaultLong
import com.curtesmalteser.ksp.annotation.WithDefaultString
import com.curtesmalteser.ksp.annotation.WithDefaultStringSet
import com.curtesmalteser.ksp.annotation.WithPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Created by António Bastião on 02.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@WithPreferences
interface AppData {
    @WithDefaultBoolean(true)
    val myBooleanFlow: Flow<Boolean>

    @WithDefaultInt(-1)
    val myIntFlow: Flow<Int>

    @WithDefaultLong(-22)
    val myLongFlow: Flow<Long>

    @WithDefaultFloat(-1.22F)
    val myFloatFlow: Flow<Float>

    @WithDefaultString("my test string!")
    val myStringFlow: Flow<String>

    @WithDefaultStringSet(["test", "this", "and that", "again", "and again"])
    val myStringSetFlow: Flow<Set<String>>

    suspend fun testBoolean(myBoolean: Boolean)
    suspend fun testInt(myInt: Int)
    suspend fun testLong(myLong: Long)
    suspend fun testFloat(myFloat: Float)
    suspend fun testString(myString: String)
    suspend fun testStringSet(myStringSet: Set<String>)
}