package com.curtesmalteser.ksp.preferences.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.curtesmalteser.ksp.preferences.ui.destination.bolean.BooleanPreferenceHandlerScreen
import com.curtesmalteser.ksp.preferences.ui.destination.reusable.*
import com.curtesmalteser.ksp.preferences.ui.host.PreferencesScreen
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme

/**
 * Created by António Bastião on 19.02.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Composable
fun PreferencesAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = PreferenceScreen.PreferencesApp.toString(),
        modifier = modifier,
    ) {
        composable(route = PreferenceScreen.PreferencesApp.toString()) {
            PreferencesScreen(navController = navController)
        }
        composable(route = PreferenceScreen.Boolean.toString()) {
            BooleanPreferenceHandlerScreen()
        }
        composable(route = PreferenceScreen.Int.toString()) {
            val viewModel : IntPreferenceHandlerViewModel = hiltViewModel()
            ReusablePreferenceHandlerScreen(viewModel)
        }
        composable(route = PreferenceScreen.Long.toString()) {
            val viewModel : LongPreferenceHandlerViewModel = hiltViewModel()
            ReusablePreferenceHandlerScreen(viewModel)

        }
        composable(route = PreferenceScreen.Float.toString()) {
            val viewModel : FloatPreferenceHandlerViewModel = hiltViewModel()
            ReusablePreferenceHandlerScreen(viewModel)

        }
        composable(route = PreferenceScreen.String.toString()) {
            val viewModel : StringPreferenceHandlerViewModel = hiltViewModel()
            ReusablePreferenceHandlerScreen(viewModel)

        }
        // FIXME: add specialized screen
        composable(route = PreferenceScreen.SetString.toString()) {
            BooleanPreferenceHandlerScreen()
        }
        // FIXME: add specialized screen
        composable(route = PreferenceScreen.UserPreferences.toString()) {
            BooleanPreferenceHandlerScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KsPreferencesTheme {
        PreferencesAppNavHost()
    }
}