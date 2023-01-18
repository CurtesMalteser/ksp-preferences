package com.curtesmalteser.ksp.preferences.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.curtesmalteser.ksp.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by António Bastião on 09.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface MainRepository : AppData

class MainRepositoryImpl(
    private val prefs: AppData,
    private val userPrefs: UserPreferencesData,
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


object UserPreferencesSerializer : Serializer<UserPreferences> {

    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences = try {
        UserPreferences.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}


