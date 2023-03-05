package com.curtesmalteser.ksp.preferences.ui.navigation

import kotlin.String as KString

/**
 * Created by António Bastião on 19.02.23
 * Refer to <a href=https://github.com/CurtesMalteser>CurtesMalteser github</a>
 */
enum class PreferenceScreen {
    PreferencesApp,
    Boolean,
    Int,
    Long,
    Float,
    String,
    SetString {
        override fun toString(): KString = "Set<String>"
    },
    UserPreferences,
}