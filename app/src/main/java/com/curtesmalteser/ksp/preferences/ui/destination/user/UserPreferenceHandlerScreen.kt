package com.curtesmalteser.ksp.preferences.ui.destination.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curtesmalteser.ksp.preferences.R
import com.curtesmalteser.ksp.preferences.data.task.TaskPriority
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme


/**
 * Created by António Bastião on 04.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Composable
fun UserPreferenceHandlerScreen(
    viewModel: UserPreferenceHandlerViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        val tasksListState = viewModel.tasksListFlow.collectAsState(initial = emptyList())

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xF2, 0xF2, 0xF2))
        ) {
            itemsIndexed(
                items = tasksListState.value,
                key = { _, v -> v.name.hashCode() }) { _, item ->
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        item.name,
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        "Priority: ${item.priority}",
                        color = when (item.priority) {
                            TaskPriority.HIGH -> Color(
                                red = 0x8F,
                                green = 0x0,
                                blue = 0x0,
                            )
                            TaskPriority.MEDIUM -> Color(red = 0xE4, green = 0x7D, blue = 0x0)
                            TaskPriority.LOW -> Color(red = 0x0, green = 0x64, blue = 0x0)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    Row {
                        Icon(
                            Icons.Filled.DateRange,
                            contentDescription = stringResource(id = R.string.update_string)
                        )

                        Text(
                            item.deadline.toString(),
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, bottom = 8.dp)
                        )
                    }
                }
                Divider(color = Color.Black)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
        ) {
            val checkedState =
                viewModel.showCompletedTasksStateFlow.collectAsState(initial = false)

            Icon(
                contentDescription = "Icon filter which indicates enabled or disabled state.",
                painter = if (checkedState.value)
                    painterResource(R.drawable.filter_list_off)
                else
                    painterResource(R.drawable.filter_list),
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
                    .padding(end = 8.dp)
            )

            Text(
                text = "Show completed tasks",
                modifier = Modifier.padding(end = 8.dp)
            )
            Switch(
                checked = checkedState.value,
                onCheckedChange = viewModel::updateShowCompleted
            )
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {

            val checkedSortByPriorityState = viewModel.isSortedByPriorityFlow
                .collectAsState(initial = false)

            val checkedSortByDeadlineState = viewModel.isSortedByDeadlineFlow
                .collectAsState(initial = false)

            SortButton(
                text = "Priority",
                state = checkedSortByPriorityState,
                onClick = viewModel::updateSortByPriority
            )
            SortButton(
                text = "Deadline",
                state = checkedSortByDeadlineState,
                onClick = viewModel::updateSortByDeadline
            )
        }
    }
}

@Composable
fun SortButton(text: String, state: State<Boolean>, onClick: () -> Unit) = Button(
    onClick = onClick,
    modifier = Modifier.padding(end = 8.dp)
) {
    Icon(
        if (state.value) Icons.Filled.Check else Icons.Filled.Close,
        contentDescription = stringResource(id = R.string.update_string)
    )
    Text(text = text, modifier = Modifier.padding(start = 8.dp))
}


@Preview(showBackground = true)
@Composable
fun UserPreferenceHandlerScreenPreview() {
    KsPreferencesTheme {
        UserPreferenceHandlerScreen()
    }
}