package com.curtesmalteser.ksp.preferences.ui.destination.reusable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults.MinHeight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme
import com.curtesmalteser.ksp.preferences.ui.utils.SharedColumn

/**
 * Created by António Bastião on 04.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
private val baseModifier by lazy {
    Modifier
        .padding(
            start = 16.dp,
            end = 16.dp
        )
        .wrapContentHeight()
        .fillMaxWidth()
}

private val shape by lazy {
    RoundedCornerShape(4.dp)
}

@Composable
fun ReusablePreferenceHandlerScreen() {
    SharedColumn {
        var value by remember { mutableStateOf("") }
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Enter text") },
            maxLines = 2,
            textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
            modifier = baseModifier.padding(bottom = 8.dp)
        )
        Text(
            text = value,
            modifier = baseModifier
                .padding(top = 8.dp)
                .clip(shape)
                .defaultMinSize(minHeight = MinHeight)
                .background(Color.LightGray)
                .padding(16.dp),
            style = TextStyle(color = Color.Blue, fontStyle = FontStyle.Italic)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReusablePreferencePreview() {
    KsPreferencesTheme {
        ReusablePreferenceHandlerScreen()
    }
}