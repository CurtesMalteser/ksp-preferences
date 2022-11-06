package com.curtesmalteser.ksp.preferences.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.curtesmalteser.ksp.preferences.UserPreferences
import com.curtesmalteser.ksp.preferences.UserPreferencesOrBuilder
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

/**
 * Created by António Bastião on 09.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface MainRepository {
    fun saveSettings()
}

class MainRepositoryImpl(
    private val prefs: DataStore<Preferences>,
    private val userPrefs: DataStore<UserPreferences>,
) : MainRepository {
    override fun saveSettings() {
        TODO("Not yet implemented")
    }

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


