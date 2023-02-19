package com.curtesmalteser.ksp.preferences.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.curtesmalteser.ksp.preferences.UserPreferences
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by António Bastião on 05.02.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
object UserPreferencesSerializer : Serializer<UserPreferences> {

    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences = try {
        UserPreferences.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) = t.writeTo(output)
}