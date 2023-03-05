package com.curtesmalteser.ksp.preferences.ui.destination.reusable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme

/**
 * Created by António Bastião on 04.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Composable
fun ReusablePreferenceHandlerScreen() {
    Text(text = "this is the reusable handler")
}

@Preview(showBackground = true)
@Composable
fun ReusablePreferencePreview() {
    KsPreferencesTheme {
        ReusablePreferenceHandlerScreen()
    }
}