package com.curtesmalteser.ksp.preferences

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.curtesmalteser.ksp.annotation.WithPreferences
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val EXAMPLE_COUNTER = intPreferencesKey("example_counter")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(this::class.java.name, "Is MainActivity? ${TestMyKspImpl(this).testmyksp}")

        lifecycleScope.launch {
            dataStore.edit { settings ->
                settings[EXAMPLE_COUNTER] = 1
            }
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
    suspend fun testInt(myInt: Int)
    suspend fun testString(myString: String)
}

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
