package ui.screens.authScreens.weights.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import ui.components.theme.text.BodyText
import ui.components.theme.text.HeadlineText
import ui.components.theme.button.TextButton

@Composable
fun DeleteDialogManager(
    value: String,
    showDeleteDialog: MutableState<Boolean>,
    dialogAction: (Boolean) -> Unit
) {
    when {
        showDeleteDialog.value -> {
            DeleteDialog(
                onDismissRequest = {
                    showDeleteDialog.value = false
                    dialogAction(false)
                },
                onConfirmation = {
                    showDeleteDialog.value = false
                    dialogAction(true)
                },
                dialogTitle = "$value löschen?",
                dialogText = "Möchtest du $value wirklich löschen?",
                icon = Icons.Default.Info
            )
        }
    }
}


@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = { Icon(icon, contentDescription = "Example Icon") },
        title = { HeadlineText(text = dialogTitle, color = MaterialTheme.colorScheme.onBackground) },
        text = { BodyText(text = dialogText, color = MaterialTheme.colorScheme.onBackground) },
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = { onDismissRequest() },
        confirmButton = { TextButton(text = "Löschen") { onConfirmation() } },
        dismissButton = { TextButton(text = "Abbruch") { onDismissRequest() } }
    )
}