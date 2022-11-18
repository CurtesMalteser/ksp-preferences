package com.curtesmalteser.ksp.preferences.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.curtesmalteser.ksp.preferences.UserPreferences

/**
 * Created by António Bastião on 14.11.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val Context.userPrefsDataStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
)