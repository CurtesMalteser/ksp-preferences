package com.curtesmalteser.ksp.preferences.ui.destination.reusable

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

/**
 * Created by António Bastião on 07.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
abstract class  ReusablePreferenceHandlerViewModel<T: Any>: ViewModel() {

    abstract val label: String

    abstract val storedValueFlow: Flow<T>

    abstract fun storeValue(value: String)
}