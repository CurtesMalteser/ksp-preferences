package com.curtesmalteser.ksp.preferences.data

import com.curtesmalteser.ksp.annotation.WithProto
import com.curtesmalteser.ksp.preferences.UserPreferences

/**
 * Created by António Bastião on 18.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@WithProto<UserPreferences>
interface UserPreferencesData {

    suspend fun x(userPreferences: UserPreferences)
}