package com.curtesmalteser.ksp.preferences.data

import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Created by António Bastião on 18.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@WithProto
interface UserPreferencesData {

    val userPreferencesFlow: Flow<UserPreferences>
    suspend fun updateUserPrefsBuilderThis(prefs: UserPreferences.Builder.() -> UserPreferences)
}