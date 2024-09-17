package ui.components.FormScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ui.components.theme.button.TextButton

/**
 * A Composable function that displays a row of buttons for canceling and saving actions.
 *
 * @param onCancel A callback that is triggered when the Cancel button is clicked.
 * @param onSave A callback that is triggered when the Save button is clicked.
 */
@Composable
fun FormControlButtons(
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(text = "Abbrechen") { onCancel() }

        TextButton(text = "Speichern") { onSave() }
    }
}