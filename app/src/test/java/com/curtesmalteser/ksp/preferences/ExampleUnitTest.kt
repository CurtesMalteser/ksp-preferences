package com.curtesmalteser.ksp.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.curtesmalteser.ksp.preferences.data.AppData
import com.curtesmalteser.ksp.preferences.data.AppDataMock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.reflect.KClass

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun implementsAppData() {
        val mock = AppDataMock(PreferencesMock())

        assertTrue(mock.isMockImplementing(AppData::class))
    }
}

class PreferencesMock : DataStore<Preferences> {
    override val data: Flow<Preferences>
        get() = flowOf()

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        TODO("Not yet implemented")
    }
}

fun <T : Any> Any.isMockImplementing(interfaceClass: KClass<T>): Boolean = interfaceClass
    .java.isAssignableFrom(this::class.java)
