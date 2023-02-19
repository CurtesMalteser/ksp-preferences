package com.curtesmalteser.ksp.preferences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KsPreferencesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    Greeting(viewModel.buttonsTextList)
                }
            }
        }
    }
}

@Composable
fun Greeting(buttonsTextList: List<String>) {
    LazyColumn {
        items(buttonsTextList.size) { index ->
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 0.dp
                    )
            ) {
                Text(text = "Test: ${buttonsTextList[index]}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KsPreferencesTheme {
        Greeting(listOf("Int", "String"))
    }
}