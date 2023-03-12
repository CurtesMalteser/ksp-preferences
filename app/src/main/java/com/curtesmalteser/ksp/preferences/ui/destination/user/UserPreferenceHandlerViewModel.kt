package com.curtesmalteser.ksp.preferences.ui.destination.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curtesmalteser.ksp.preferences.UserPreferences
import com.curtesmalteser.ksp.preferences.data.task.Task
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
    private val useCase: UserPreferenceHandlerUseCase,
) : ViewModel() {

    private val _showCompletedTasksStateFlow = MutableStateFlow(false)
    val showCompletedTasksStateFlow: MutableStateFlow<Boolean> get() = _showCompletedTasksStateFlow

    private val _isSortedByDeadlineFlow = MutableStateFlow(false)
    val isSortedByDeadlineFlow: MutableStateFlow<Boolean> get() = _isSortedByDeadlineFlow

    private val _isSortedByPriorityFlow = MutableStateFlow(false)
    val isSortedByPriorityFlow: MutableStateFlow<Boolean> get() = _isSortedByPriorityFlow

    private val _tasksListFlow = MutableStateFlow(emptyList<Task>())
    val tasksListFlow: MutableStateFlow<List<Task>> get() = _tasksListFlow

    init {
        viewModelScope.launch {
            useCase.tasksUiModelFlow
                .collect { uiModel ->

                    _tasksListFlow.update { uiModel.tasks }

                    _showCompletedTasksStateFlow.update { uiModel.showCompleted }

                    when (uiModel.sortOrder) {

                        UserPreferences.SortOrder.BY_DEADLINE -> updateSortedBy(
                            deadline = true,
                            priority = false
                        )
                        UserPreferences.SortOrder.BY_PRIORITY -> updateSortedBy(
                            priority = true,
                            deadline = false
                        )
                        UserPreferences.SortOrder.BY_DEADLINE_AND_PRIORITY -> updateSortedBy(
                            priority = true,
                            deadline = true
                        )
                        else -> updateSortedBy(priority = false, deadline = false)
                    }
                }
        }
    }

    private fun updateSortedBy(deadline: Boolean, priority: Boolean) {
        _isSortedByDeadlineFlow.update { deadline }
        _isSortedByPriorityFlow.update { priority }
    }

    fun updateShowCompleted(isShow: Boolean) {
        viewModelScope.launch {
            useCase.showCompletedTasks(isShow)
        }
    }

    fun updateSortByDeadline() {
        viewModelScope.launch {
            useCase.enableSortByDeadline(!isSortedByDeadlineFlow.value)
        }
    }

    fun updateSortByPriority() {
        viewModelScope.launch {
            useCase.enableSortByPriority(!isSortedByPriorityFlow.value)
        }
    }

}