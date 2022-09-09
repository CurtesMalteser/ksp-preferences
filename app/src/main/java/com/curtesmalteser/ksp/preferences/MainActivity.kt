package com.curtesmalteser.ksp.preferences

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val test = TestMyKspImpl(this)

        lifecycleScope.launch {
            test.testInt(2)
        }

        setContent {
            KsPreferencesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KsPreferencesTheme {
        Greeting("Android")
    }
}

@WithPreferences
interface TestMyKsp {
    val myBooleanFlow: Flow<Boolean>
    val myIntFlow: Flow<Int>
    val myLongFlow: Flow<Long>
    val myFloatFlow: Flow<Float>
    val myStringFlow: Flow<String>
    val myStringSetFlow: Flow<Set<String>>
    suspend fun testBoolean(myBoolean: Boolean)
    suspend fun testInt(myInt: Int)
    suspend fun testLong(myLong: Long)
    suspend fun testFloat(myFloat: Float)
    suspend fun testString(myString: String)
    suspend fun testStringSet(myStringSet: Set<String>)
}