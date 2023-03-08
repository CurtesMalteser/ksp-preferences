package com.curtesmalteser.ksp.preferences.ui.destination.stringset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults.MinHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curtesmalteser.ksp.preferences.R
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme

/**
 * Created by António Bastião on 04.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */

private val shape by lazy {
    RoundedCornerShape(4.dp)
}

@Composable
fun StringSetPreferenceHandlerScreen(
    viewModel: StringSetPreferenceHandlerViewModel = hiltViewModel()
) {

    val storedValue = viewModel.storedValueFlow.collectAsState(initial = emptySet())

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = storedValue.value.toString(),
                onValueChange = { viewModel.storeValue(it) },
                label = { Text(viewModel.label) },
                maxLines = 1,
                textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(end = 16.dp)
            )
            Button(
                onClick = {
                    viewModel.storeValue("test")
                },
                modifier = Modifier
                    .height(MinHeight)
                    .defaultMinSize(minWidth = MinHeight)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_string)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(storedValue.value.size) { index ->
                Text(
                    text = storedValue.value.map { it }[index],
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .defaultMinSize(minHeight = MinHeight)
                        .background(Color.LightGray)
                        .padding(16.dp),
                    style = TextStyle(color = Color.Blue, fontStyle = FontStyle.Italic)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReusablePreferencePreview() {
    KsPreferencesTheme {
        StringSetPreferenceHandlerScreen()
    }
}