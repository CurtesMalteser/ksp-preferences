package com.curtesmalteser.ksp.preferences.ui.destination.stringset

import com.curtesmalteser.ksp.preferences.data.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by António Bastião on 08.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
interface StringSetHandlerUseCase {
    val setOfStringFlow: Flow<Set<String>>
    suspend fun storeString(value: String)
    suspend fun deleteString(value: String)
}

class StringSetHandlerUseCaseImpl @Inject constructor(private val repository: MainRepository) :
    StringSetHandlerUseCase {

    private var state: Set<String> = emptySet()

    override val setOfStringFlow: Flow<Set<String>>
        get() = repository.myStringSetFlow
            .onEach { state = it }

    override suspend fun storeString(value: String) {
        repository.testStringSet(state.toMutableSet().apply { add(value) })
    }

    override suspend fun deleteString(value: String) {
        repository.testStringSet(state.filterNot { it == value }.toSet())
    }

}