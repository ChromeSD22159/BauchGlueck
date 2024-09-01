package de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector

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
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Löschen")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Abbruch")
            }
        }
    )
}