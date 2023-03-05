package com.curtesmalteser.ksp.preferences

import androidx.lifecycle.ViewModel
import com.curtesmalteser.ksp.preferences.ui.navigation.PreferenceScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by António Bastião on 02.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    val buttonsTextList: List<PreferenceScreen>
        get() = PreferenceScreen.values().filterNot {
            it == PreferenceScreen.PreferencesApp
        }

}