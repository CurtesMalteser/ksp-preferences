package com.curtesmalteser.ksp.preferences.ui.destination.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curtesmalteser.ksp.preferences.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by António Bastião on 12.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@HiltViewModel
class UserPreferenceHandlerViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _showCompletedState = MutableStateFlow(false)
    val showCompletedState: MutableStateFlow<Boolean> get() = _showCompletedState

    init {
        viewModelScope.launch {
            userPreferencesRepository.getShowCompleted
                .collect { isShow ->
                    _showCompletedState.update { isShow }

                    Log.d(
                        this@UserPreferenceHandlerViewModel::class.java.simpleName,
                        "onSuccess: $isShow"
                    )
                }
        }
    }

    fun updateShowCompleted(isShow: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateShowCompleted(isShow)
        }
    }

}