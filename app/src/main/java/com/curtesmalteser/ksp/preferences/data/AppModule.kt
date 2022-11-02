package com.curtesmalteser.ksp.preferences.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.curtesmalteser.ksp.preferences.App
import com.curtesmalteser.ksp.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by António Bastião on 02.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val Context.settingsDataStore: DataStore<UserPreferences> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
)

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun appPreferences(app: App) : DataStore<Preferences> = app.dataStore

    @Provides
    fun appData(dataStore: DataStore<Preferences>): AppData = AppDataImpl(dataStore)
}