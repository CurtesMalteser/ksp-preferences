package com.curtesmalteser.ksp.preferences.ui.utils

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Created by António Bastião on 07.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Composable
fun SharedColumn(content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.Start,
    content = content
)

@Composable
fun SharedScreen(content: @Composable RowScope.() -> Unit) = SharedColumn {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp),
        content = content
    )
}