package com.curtesmalteser.ksp.preferences.ui.destination.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curtesmalteser.ksp.preferences.R
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
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Column(Modifier.fillMaxSize()) {
            val tasksListState = viewModel.tasksListFlow.collectAsState(initial = emptyList())

            LazyColumn {
                itemsIndexed(
                    items = tasksListState.value,
                    key = { _, v -> v.name.hashCode() }) { _, item ->
                    Text(
                        item.name,
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                val checkedState =
                    viewModel.showCompletedTasksStateFlow.collectAsState(initial = false)

                Icon(
                    painter = if (checkedState.value)
                        painterResource(R.drawable.filter_list)
                    else
                        painterResource(R.drawable.filter_list_off),
                    contentDescription = "Icon filter which indicates enabled or disabled state.",
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
                modifier = Modifier.fillMaxWidth()
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