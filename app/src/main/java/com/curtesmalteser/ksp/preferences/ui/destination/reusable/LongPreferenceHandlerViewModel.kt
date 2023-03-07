package com.curtesmalteser.ksp.preferences.ui.destination.reusable

import androidx.lifecycle.viewModelScope
import com.curtesmalteser.ksp.preferences.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by António Bastião on 07.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@HiltViewModel
class LongPreferenceHandlerViewModel @Inject constructor(private val repository: MainRepository) :
    ReusablePreferenceHandlerViewModel<Long>() {

    override val label: String = "Long"

    override val storedValueFlow: Flow<Long> get() = repository.myLongFlow

    override fun storeValue(value: String) {
        viewModelScope.launch {
            val longNumber = value.toLongOrNull() ?: 0
            repository.testLong(longNumber)
        }
    }
}