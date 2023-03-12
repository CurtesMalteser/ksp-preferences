package com.curtesmalteser.ksp.preferences.data

import com.curtesmalteser.ksp.preferences.UserPreferences
import com.curtesmalteser.ksp.preferences.UserPreferences.SortOrder
import com.curtesmalteser.ksp.preferences.UserPreferences.getDefaultInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by António Bastião on 09.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface UserPreferencesRepository {
    val userPreferencesFlow: Flow<UserPreferences>

    suspend fun updateShowCompleted(isShow: Boolean)
    suspend fun enableSortByDeadline(enable: Boolean)
    suspend fun enableSortByPriority(enable: Boolean)
}

class UserPreferencesRepositoryImpl(
    private val userPrefs: UserPreferencesData,
) : UserPreferencesRepository {

    override val userPreferencesFlow: Flow<UserPreferences> = runCatching {
        userPrefs.userPreferencesFlow
    }.fold(
        onSuccess = { it },
        onFailure = {
            flow {
                emit(getDefaultInstance())
            }
        }
    )

    override suspend fun updateShowCompleted(isShow: Boolean) {
        userPrefs.updateUserPrefsBuilderThis {
            setShowCompleted(isShow).build()
        }
    }

    override suspend fun enableSortByDeadline(enable: Boolean) {
        userPrefs.updateUserPrefsBuilderThis {
            val newSortOrder =
                sortOrderUpdate(
                    enable = enable,
                    toEnable = SortOrder.BY_DEADLINE,
                    toVerify = SortOrder.BY_PRIORITY
                )
            setSortOrder(newSortOrder).build()
        }
    }

    override suspend fun enableSortByPriority(enable: Boolean) {
        userPrefs.updateUserPrefsBuilderThis {
            val newSortOrder = sortOrderUpdate(
                enable = enable,
                toEnable = SortOrder.BY_PRIORITY,
                toVerify = SortOrder.BY_DEADLINE
            )
            setSortOrder(newSortOrder).build()
        }
    }

    private fun UserPreferences.Builder.sortOrderUpdate(
        enable: Boolean,
        toEnable: SortOrder,
        toVerify: SortOrder
    ) = if (enable) {
        if (sortOrder == toVerify) {
            SortOrder.BY_DEADLINE_AND_PRIORITY
        } else {
            toEnable
        }
    } else {
        if (sortOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
            toVerify
        } else {
            SortOrder.NONE
        }
    }
}


