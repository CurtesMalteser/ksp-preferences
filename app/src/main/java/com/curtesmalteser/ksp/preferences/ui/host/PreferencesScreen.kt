package com.curtesmalteser.ksp.preferences.ui.host

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.curtesmalteser.ksp.preferences.MainActivityViewModel
import com.curtesmalteser.ksp.preferences.ui.navigation.PreferencesAppNavHost
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme

/**
 * Created by António Bastião on 04.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Composable
fun PreferencesApp(modifier: Modifier = Modifier) {

    Scaffold {innerPadding ->
        PreferencesAppNavHost( modifier = modifier.padding(innerPadding))
    }
}

@Composable
fun PreferencesScreen(modifier: Modifier = Modifier,
                   viewModel: MainActivityViewModel = viewModel(),
                   navController: NavHostController) {

    val buttonsTextList = viewModel.buttonsTextList

    LazyColumn {
        items(buttonsTextList.size) { index ->
            Button(
                onClick = { navController.navigate(buttonsTextList[index].toString())},
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
        PreferencesApp()
    }
}