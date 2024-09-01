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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.ui.components.ItemOverLayScaffold
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.androidx.compose.koinViewModel
import org.lighthousegames.logging.logging
import util.generateDeviceId
import viewModel.TimerScreenViewModel

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddEditTimerSheet(
    navController: NavController,
    currentCountdownTimer: String? = null,
    onDismiss: () -> Unit = {},
) {
    val viewModel = koinViewModel<TimerScreenViewModel>()

    LaunchedEffect(Unit) {
        viewModel.getTimerByIdOrNull(currentCountdownTimer)
    }

    val selectedTimer by viewModel.selectedTimer.collectAsState()
    var isClicked by remember { mutableStateOf(false) }

    var newOrUpdatedTimer: CountdownTimer by remember {
        mutableStateOf(selectedTimer ?: CountdownTimer(
            timerId = generateDeviceId(),
            userId = Firebase.auth.currentUser?.uid ?: "",
        ))
    }


    val text = remember { mutableStateOf(newOrUpdatedTimer.name) }
    val duration = remember { mutableLongStateOf(newOrUpdatedTimer.duration) }

    LaunchedEffect(selectedTimer) { // Move state initialization inside the LaunchedEffect
       newOrUpdatedTimer = selectedTimer?.let {
            it.copy(duration = it.duration / 60)
        } ?: CountdownTimer(
            timerId = generateDeviceId(),
            userId = Firebase.auth.currentUser?.uid ?: "",
        )

        logging().info { "selectedTimer: $selectedTimer" }
        logging().info { "newOrUpdatedTimer: $newOrUpdatedTimer" }

        text.value = newOrUpdatedTimer.name
        duration.longValue = newOrUpdatedTimer.duration

    }



    val focusManager = LocalFocusManager.current
    val isNameFocused = remember { mutableStateOf(false) }
    val isDurationFocused = remember { mutableStateOf(false) }


    LaunchedEffect(isClicked) {
        if (isClicked) {
            viewModel.updateItemAndSyncRemote(newOrUpdatedTimer.copy(name = text.value, duration = duration.longValue * 60))
        }
    }
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
                focusManager.clearFocus()

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

                    onDismiss()
                }
            ) {
                Text("Abbrechen")
            }

            Button(
                onClick = {
                    if (!isClicked) {
                        focusManager.clearFocus()

                        isClicked = true

                        onDismiss()
                    }
                }
            ) {
                    Text("Speichern")
            }

        }
    }
}