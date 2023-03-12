package com.curtesmalteser.ksp.preferences.ui.destination.stringset

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.TextFieldDefaults.MinHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            var text by rememberSaveable { mutableStateOf("") }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(viewModel.label) },
                maxLines = 1,
                textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(end = 16.dp)
                    .width(IntrinsicSize.Min)
            )
            Button(
                onClick = {
                    viewModel.storeValue(text)
                },
                modifier = Modifier.height(MinHeight)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.add_string)
                )
            }
        }

        val storedValue = viewModel.storedValueFlow.collectAsState(emptySet())

        LazyColumn(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            itemsIndexed(
                items = storedValue.value.toList(),
                key = { _, s -> s.hashCode() }) { _, item ->
                SwipeToDismissPreference(item) { viewModel.isDelete(it) }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun SwipeToDismissPreference(
    item: String,
    deleteAction: (String) -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToEnd,
                DismissValue.DismissedToStart -> {
                    deleteAction(item)
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(state = dismissState, background = {
        val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
        val color by animateColorAsState(
            when (dismissState.targetValue) {
                Default -> Color.Gray
                DismissValue.DismissedToEnd,
                DismissValue.DismissedToStart -> Color.Red
            }
        )
        val alignment = when (direction) {
            DismissDirection.StartToEnd -> Alignment.CenterStart
            DismissDirection.EndToStart -> Alignment.CenterEnd
        }
        val icon = when (direction) {
            DismissDirection.StartToEnd,
            DismissDirection.EndToStart -> Icons.Default.Delete
        }
        val scale by animateFloatAsState(
            if (dismissState.targetValue == Default) 0.75f else 1f
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(color)
                .padding(horizontal = 20.dp),
            contentAlignment = alignment
        ) {
            Icon(
                icon,
                contentDescription = "Red color dismiss box.",
                modifier = Modifier.scale(scale)
            )
        }
    }) {
        Text(
            text = item,
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

@Preview(showBackground = true)
@Composable
fun ReusablePreferencePreview() {
    KsPreferencesTheme {
        StringSetPreferenceHandlerScreen()
    }
}