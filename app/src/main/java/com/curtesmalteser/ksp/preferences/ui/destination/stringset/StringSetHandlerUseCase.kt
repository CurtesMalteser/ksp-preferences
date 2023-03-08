package com.curtesmalteser.ksp.preferences.ui.destination.stringset

import com.curtesmalteser.ksp.preferences.data.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by António Bastião on 08.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface StringSetHandlerUseCase {
    val setOfStringFlow: Flow<Set<String>>
    suspend fun storeString(value: String)
}

class StringSetHandlerUseCaseImpl @Inject constructor(private val repository: MainRepository) :
    StringSetHandlerUseCase {
    override val setOfStringFlow: Flow<Set<String>>
        get() = repository.myStringSetFlow

    override suspend fun storeString(value: String) {
        repository.testStringSet(setOf(value))
    }

}