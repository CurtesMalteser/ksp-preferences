package com.curtesmalteser.ksp.preferences.ui.destination.user

import com.curtesmalteser.ksp.preferences.UserPreferences
import com.curtesmalteser.ksp.preferences.UserPreferences.SortOrder.*
import com.curtesmalteser.ksp.preferences.data.UserPreferencesRepository
import com.curtesmalteser.ksp.preferences.data.task.Task
import com.curtesmalteser.ksp.preferences.data.task.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Created by António Bastião on 08.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
data class TasksUiModel(
    val tasks: List<Task>,
    val showCompleted: Boolean,
    val sortOrder: UserPreferences.SortOrder
)

interface UserPreferenceHandlerUseCase {

    val tasksUiModelFlow: Flow<TasksUiModel>

    suspend fun showCompletedTasks(show: Boolean)

    suspend fun enableSortByDeadline(enable: Boolean)

    suspend fun enableSortByPriority(enable: Boolean)
}

class UserPreferenceHandlerUseCaseImpl @Inject constructor(
    tasksRepository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : UserPreferenceHandlerUseCase {

    private val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow

    override val tasksUiModelFlow = combine(
        tasksRepository.tasks,
        userPreferencesFlow
    ) { tasks: List<Task>, userPreferences: UserPreferences ->
        TasksUiModel(
            tasks = filterSortTasks(
                tasks,
                userPreferences.showCompleted,
                userPreferences.sortOrder
            ),
            showCompleted = userPreferences.showCompleted,
            sortOrder = userPreferences.sortOrder
        )
    }

    private fun filterSortTasks(
        tasks: List<Task>,
        showCompleted: Boolean,
        sortOrder: UserPreferences.SortOrder
    ): List<Task> {
        val filteredTasks = if (showCompleted) {
            tasks
        } else {
            tasks.filter { !it.completed }
        }
        return when (sortOrder) {
            UNSPECIFIED -> filteredTasks
            NONE -> filteredTasks
            BY_DEADLINE -> filteredTasks.sortedByDescending { it.deadline }
            BY_PRIORITY -> filteredTasks.sortedBy { it.priority }
            BY_DEADLINE_AND_PRIORITY -> filteredTasks.sortedWith(
                compareByDescending<Task> { it.deadline }.thenBy { it.priority }
            )
            else -> throw UnsupportedOperationException("$sortOrder not supported")
        }
    }

    override suspend fun showCompletedTasks(show: Boolean) =
        userPreferencesRepository.updateShowCompleted(show)

    override suspend fun enableSortByDeadline(enable: Boolean) =
        userPreferencesRepository.enableSortByDeadline(enable)

    override suspend fun enableSortByPriority(enable: Boolean) =
        userPreferencesRepository.enableSortByPriority(enable)

}