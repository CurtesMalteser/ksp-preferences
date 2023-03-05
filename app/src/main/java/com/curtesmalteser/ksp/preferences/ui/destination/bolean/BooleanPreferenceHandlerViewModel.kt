package com.curtesmalteser.ksp.preferences.ui.destination.bolean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curtesmalteser.ksp.preferences.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by António Bastião on 05.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@HiltViewModel
class BooleanPreferenceHandlerViewModel @Inject constructor(private val repository: MainRepository) :
    ViewModel() {

    val checkedState: Flow<Boolean> get() = repository.myBooleanFlow

    fun storeCheckedState(isChecked: Boolean) {
        viewModelScope.launch {
            repository.testBoolean(isChecked)
        }
    }
}