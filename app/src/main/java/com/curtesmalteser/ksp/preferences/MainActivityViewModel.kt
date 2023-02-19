package com.curtesmalteser.ksp.preferences

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by António Bastião on 02.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {

    val buttonsTextList by lazy {
        listOf(
            "Boolean",
            "Int",
            "Long",
            "Float",
            "String",
            "Set<String>",
            "UserPreferences",
        )
    }

}