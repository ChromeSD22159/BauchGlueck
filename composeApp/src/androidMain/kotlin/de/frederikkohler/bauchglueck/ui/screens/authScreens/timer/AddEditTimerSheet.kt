package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.ui.components.ItemOverLayScaffold
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import de.frederikkohler.bauchglueck.ui.theme.AppTheme

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddEditTimerSheet(
    navController: NavController,
    currentCountdownTimer: CountdownTimer? = null,
    onDismiss: () -> Unit = {},
    onSaved: (CountdownTimer) -> Unit = {}
) {
    val countdownTimer: CountdownTimer = currentCountdownTimer?.copy() ?: CountdownTimer()
    val focusManager = LocalFocusManager.current
    val isNameFocused = remember { mutableStateOf(false) }
    val isDurationFocused = remember { mutableStateOf(false) }

    val text = remember { mutableStateOf(countdownTimer.name) }
    val duration = remember { mutableLongStateOf(countdownTimer.duration) }

    val colors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        focusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
        focusedContainerColor = Color.Gray.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    )

    ItemOverLayScaffold(
        title = "Neuen Timer hinzufügen",
        topNavigationButtons = {
            IconButton(onClick = {
                navController.navigate(Destination.Timer.route)
                onDismiss()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Localized description"
                )
            }
        },
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TextField(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Localized description"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                value = text.value,
                colors = colors,
                onValueChange = {
                    text.value = it
                }
            )

            Text(
                text = "Dient zur bessern zu differenzierung.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TextField(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Localized description"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                value = duration.longValue.toString(),
                colors = colors,
                onValueChange = { input ->
                    duration.longValue = input.toLongOrNull() ?: 0L
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(
                text = "Timerlaufzeit in Minuten",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    isNameFocused.value = false
                    isDurationFocused.value = false

                    focusManager.clearFocus()
                    navController.navigate(Destination.Timer.route)
                    onDismiss()
                }
            ) {
                Text("Abbrechen")
            }

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSaved(countdownTimer.copy(name = text.value, duration = duration.longValue))

                    focusManager.clearFocus()
                    navController.navigate(Destination.Timer.route)
                }
            ) {
                    Text("Speichern")
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddTimerPreview() {
    AppTheme {
        AddEditTimerSheet(navController = NavController(LocalContext.current))
    }
}