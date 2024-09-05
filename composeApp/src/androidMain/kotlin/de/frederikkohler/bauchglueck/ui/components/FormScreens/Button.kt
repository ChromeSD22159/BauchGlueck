package de.frederikkohler.bauchglueck.ui.components.FormScreens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * A Composable function that displays a button with a given display text and onClick action.
 */
@Composable
fun Button(
    displayText: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.Button(
        onClick = { onClick() }
    ) {
        Text(displayText)
    }
}