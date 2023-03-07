package com.curtesmalteser.ksp.preferences.ui.destination.bolean

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curtesmalteser.ksp.preferences.ui.theme.KsPreferencesTheme
import com.curtesmalteser.ksp.preferences.ui.utils.SharedScreen

/**
 * Created by António Bastião on 04.03.23
 * Refer to <a href="https://github.com/CurtesMalteser">CurtesMalteser github</a>
 */
@Composable
fun BooleanPreferenceHandlerScreen(
    viewModel: BooleanPreferenceHandlerViewModel = hiltViewModel(),
) {

    val state = viewModel.checkedState.collectAsState(initial = false)

    val transition = updateTransition(targetState = state.value, label = "")

    val color by transition.animateColor(label = "") {
        if (it) Color.Green else Color.Red
    }

    val cornerRadius by transition.animateDp(label = "",
        transitionSpec = {
            if (targetState)
                spring(dampingRatio = Spring.DampingRatioHighBouncy)
            else
                spring(dampingRatio = Spring.DampingRatioNoBouncy)
        }) { if (it) 48.dp else 8.dp }

    val shape = RoundedCornerShape(
        topStart = cornerRadius,
        topEnd = 8.dp,
        bottomEnd = 8.dp,
        bottomStart = 8.dp
    )

    SharedScreen {
            Checkbox(checked = state.value,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.White,
                    checkmarkColor = Color.Green,
                    disabledColor = Color.White,
                    disabledIndeterminateColor = Color.White,
                ),
                modifier = Modifier
                    .size(96.dp)
                    .clip(shape)
                    .background(color),
                onCheckedChange = {
                    viewModel.storeCheckedState(it)
                })

            Text(
                text = state.value.let {
                    if (it)
                        "Checkbox is checked!"
                    else
                        "Checkbox is unchecked!"
                },
                modifier = Modifier.padding(start = 16.dp)
            )
    }
}

@Preview(showBackground = true)
@Composable
fun BooleanPreferencePreview() {
    KsPreferencesTheme {
        BooleanPreferenceHandlerScreen()
    }
}