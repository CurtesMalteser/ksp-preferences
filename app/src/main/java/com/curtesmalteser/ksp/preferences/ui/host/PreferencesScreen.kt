package com.curtesmalteser.ksp.preferences.ui.host

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.curtesmalteser.ksp.preferences.MainActivityViewModel
import com.curtesmalteser.ksp.preferences.ui.navigation.PreferenceScreen
import com.curtesmalteser.ksp.preferences.ui.navigation.PreferencesAppNavHost
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme

/**
 * Created by António Bastião on 04.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Composable
fun PreferencesApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = PreferenceScreen.valueOf(
        backStackEntry?.destination?.route ?: PreferenceScreen.PreferencesApp.name
    )

    Scaffold(topBar = {
        PreferencesAppBar(
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() }
        )
    }) { innerPadding ->
        PreferencesAppNavHost(
            modifier = modifier.padding(innerPadding),
            navController = navController,
        )
    }
}

@Composable
fun PreferencesAppBar(
    currentScreen: PreferenceScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(currentScreen.name) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
        }
    )
}

@Composable
fun PreferencesScreen(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel(),
    navController: NavHostController
) {

    val buttonsTextList = viewModel.buttonsTextList

    LazyColumn {
        items(buttonsTextList.size) { index ->
            Button(
                onClick = { navController.navigate(buttonsTextList[index].name) },
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