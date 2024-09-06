package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.FormScreens.FormControlButtons
import de.frederikkohler.bauchglueck.ui.components.FormScreens.FormTextFieldRow
import de.frederikkohler.bauchglueck.ui.components.ItemOverLayScaffold
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.androidx.compose.koinViewModel
import org.lighthousegames.logging.logging
import util.generateId
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
            timerId = generateId(),
            userId = Firebase.auth.currentUser?.uid ?: "",
        ))
    }


    val text = remember { mutableStateOf(newOrUpdatedTimer.name) }
    val duration = remember { mutableLongStateOf(newOrUpdatedTimer.duration) }

    LaunchedEffect(selectedTimer) { // Move state initialization inside the LaunchedEffect
       newOrUpdatedTimer = selectedTimer?.let {
            it.copy(duration = it.duration / 60)
        } ?: CountdownTimer(
            timerId = generateId(),
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

        FormTextFieldRow(
            keyboardType = KeyboardType.Text,
            leadingIcon = R.drawable.ic_stopwatch,
            inputValue = text.value,
            displayText = "Dient zur bessern zu differenzierung.",
            onValueChange = { text.value = it }
        )

        FormTextFieldRow(
            keyboardType = KeyboardType.Number,
            leadingIcon = R.drawable.ic_stopwatch,
            inputValue = duration.longValue.toString(),
            displayText = "Timerlaufzeit in Minuten",
            onValueChange = {
                duration.longValue = it.toLongOrNull() ?: 0L
            }
        )

        FormControlButtons(
            onSave = {
                if (!isClicked) {
                    focusManager.clearFocus()

                    isClicked = true

                    onDismiss()
                }
            },
            onCancel = {
                isNameFocused.value = false
                isDurationFocused.value = false

                focusManager.clearFocus()

                onDismiss()
            },
        )
    }
}
