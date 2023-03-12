package com.curtesmalteser.ksp.preferences.data

import com.curtesmalteser.ksp.preferences.UserPreferences
import com.curtesmalteser.ksp.preferences.UserPreferences.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Created by António Bastião on 09.10.22
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface UserPreferencesRepository {
    val getShowCompleted: Flow<Boolean>
    suspend fun updateShowCompleted(isShow: Boolean)
    suspend fun enableSortByDeadline(enable: Boolean)
    suspend fun enableSortByPriority(enable: Boolean)
}

class UserPreferencesRepositoryImpl(
    private val userPrefs: UserPreferencesData,
) : UserPreferencesRepository {

    override val getShowCompleted: Flow<Boolean> = runCatching {
        userPrefs.userPreferencesFlow
    }.fold(
        onSuccess = {
            it.map(UserPreferences::getShowCompleted)
        }, onFailure = {
            flow {
                emit(false)
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


