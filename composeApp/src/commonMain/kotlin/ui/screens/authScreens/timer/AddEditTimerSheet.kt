package ui.screens.authScreens.timer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_stopwatch
import data.local.entitiy.CountdownTimer
import ui.components.FormScreens.FormControlButtons
import ui.components.FormScreens.FormTextFieldRow
import ui.components.ItemOverLayScaffold
import ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import ui.components.FormScreens.DescriptionText
import ui.components.FormScreens.PlusMinusButtonForms
import ui.navigations.NavigationTransition
import util.UUID
import util.formatTimeToMMSS
import viewModel.TimerScreenViewModel

data class TimerSettings(
    val minDuration: Int = 5,
    val maxDuration: Int = 90,
    val steps: Int = 5
)


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.editTimerComposable(navController: NavHostController) {
    composable(
        route = Destination.EditTimer.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditTimerSheet(
            navController = navController,
            currentCountdownTimer = navController.currentBackStackEntry?.savedStateHandle?.get<String>("timerId"),
            onDismiss = {
                navController.navigate(Destination.Timer.route)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addTimerComposable(navController: NavHostController) {
    composable(
        route = Destination.AddTimer.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditTimerSheet(
            navController = navController,
            currentCountdownTimer = null,
            onDismiss = {
                navController.navigate(Destination.Timer.route)
            }
        )
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddEditTimerSheet(
    navController: NavController,
    currentCountdownTimer: String? = null,
    timerRange: TimerSettings = TimerSettings(),
    onDismiss: () -> Unit = {},
) {
    val viewModel = viewModel<TimerScreenViewModel>()

    LaunchedEffect(Unit) {
        viewModel.getTimerByIdOrNull(currentCountdownTimer)
    }

    val selectedTimer by viewModel.selectedTimer.collectAsState()
    var isClicked by remember { mutableStateOf(false) }

    var newOrUpdatedTimer: CountdownTimer by remember {
        mutableStateOf(selectedTimer ?: CountdownTimer(
            timerId = UUID.randomUUID(),
            userId = Firebase.auth.currentUser?.uid ?: "",
        ))
    }


    val text = remember { mutableStateOf(newOrUpdatedTimer.name) }
    val duration = remember { mutableLongStateOf(newOrUpdatedTimer.duration) }

    LaunchedEffect(selectedTimer) { // Move state initialization inside the LaunchedEffect
       newOrUpdatedTimer = selectedTimer?.let {
            it.copy(duration = it.duration / 60)
        } ?: CountdownTimer(
            timerId = UUID.randomUUID(),
            userId = Firebase.auth.currentUser?.uid ?: "",
        )

        text.value = newOrUpdatedTimer.name
        duration.longValue = newOrUpdatedTimer.duration
    }

    val focusManager = LocalFocusManager.current
    val isNameFocused = remember { mutableStateOf(false) }
    val isDurationFocused = remember { mutableStateOf(false) }

    ItemOverLayScaffold(
        title = if(currentCountdownTimer == null) "Neuen Timer hinzufügen" else "Timer bearbeiten",
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
            leadingIcon = Res.drawable.ic_stopwatch,
            inputValue = text.value,
            displayText = "Dient zur bessern zu differenzierung.",
            onValueChange = { text.value = it }
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PlusMinusButtonForms(
                displayColumn = {
                    Text(duration.longValue.formatTimeToMMSS())
                },
                onMinus = {
                    if (duration.longValue > timerRange.minDuration) {
                        duration.longValue -= timerRange.steps
                    }
                },
                onPlus = {
                    if (duration.longValue < timerRange.maxDuration) {
                        duration.longValue += timerRange.steps
                    }
                }
            )

            DescriptionText("Timerlaufzeit in Minuten")
        }

        Spacer(Modifier.height(10.dp))

        FormControlButtons(
            onSave = {
                if (!isClicked) {
                    focusManager.clearFocus()

                    viewModel.updateItemAndSyncRemote(
                        newOrUpdatedTimer.copy(
                            name = text.value,
                            duration = duration.longValue * 60
                        )
                    )

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
