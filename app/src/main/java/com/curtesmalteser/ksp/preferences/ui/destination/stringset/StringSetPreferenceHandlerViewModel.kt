package com.curtesmalteser.ksp.preferences.ui.destination.stringset

import androidx.lifecycle.viewModelScope
import com.curtesmalteser.ksp.preferences.ui.destination.reusable.ReusablePreferenceHandlerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by António Bastião on 07.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@HiltViewModel
class StringSetPreferenceHandlerViewModel @Inject constructor(private val useCase: StringSetHandlerUseCase) :
    ReusablePreferenceHandlerViewModel<Set<String>>() {

    override val label: String = "String"

    override val storedValueFlow: Flow<Set<String>> get() = useCase.setOfStringFlow

    override fun storeValue(value: String) {
        viewModelScope.launch {
            useCase.storeString(value)
        }
    }
}