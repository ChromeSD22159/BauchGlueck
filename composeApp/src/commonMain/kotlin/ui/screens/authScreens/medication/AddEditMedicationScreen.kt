package ui.screens.authScreens.medication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_description
import data.local.entitiy.IntakeTime
import data.local.entitiy.IntakeTimeWithStatus
import data.local.entitiy.Medication
import data.local.entitiy.MedicationWithIntakeDetails
import ui.components.FormScreens.FormControlButtons
import ui.components.FormScreens.FormTextFieldRow
import ui.components.ItemOverLayScaffold
import ui.components.theme.clickableWithRipple
import ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.lighthousegames.logging.logging
import ui.components.theme.sectionShadow
import ui.components.theme.text.BodyText
import ui.navigations.NavigationTransition
import util.UUID
import viewModel.MedicationViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addMedication(navController: NavHostController){
    composable(
        route = Destination.AddMedication.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditMedicationScreen(
            navController = navController,
            currentMedication = null
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.editMedication(navController: NavHostController) {
    composable(
        route = Destination.EditMedication.route,
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() },
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
    ) {
        AddEditMedicationScreen(
            navController = navController,
            currentMedication = navController.currentBackStackEntry?.savedStateHandle?.get<String>("medicationId")
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEditMedicationScreen(
    navController: NavHostController,
    currentMedication: String? = null
) {
    val viewModel = viewModel<MedicationViewModel>()

    var newOrUpdatedTimer: MedicationWithIntakeDetails by remember {
        mutableStateOf( MedicationWithIntakeDetails(
            medication = Medication(
                id = 0,
                medicationId = UUID.randomUUID(),
                userId = Firebase.auth.currentUser?.uid ?: "",
                dosage = "",
                name = "",
                isDeleted = false,
                updatedAtOnDevice = Clock.System.now().toEpochMilliseconds(),
            ),
            intakeTimesWithStatus = emptyList()
        ))
    }

    val medicationName = remember { mutableStateOf(newOrUpdatedTimer.medication.name) }
    val medicationDosage = remember { mutableStateOf(newOrUpdatedTimer.medication.name) }

    LaunchedEffect(Unit) {
        viewModel.viewModelScope.launch {
            if(currentMedication != null) {
                viewModel.getMedicationsWithIntakeTimesForTodayByMedicationID(currentMedication).collect {
                    newOrUpdatedTimer = MedicationWithIntakeDetails(
                        medication = it.medication,
                        intakeTimesWithStatus = it.intakeTimesWithStatus
                    )
                    medicationName.value = it.medication.name
                    medicationDosage.value = it.medication.dosage
                }
            }
        }
    }

    val focusManager = LocalFocusManager.current
    val isNameFocused = remember { mutableStateOf(false) }
    val isDosageFocused = remember { mutableStateOf(false) }

    ItemOverLayScaffold(
        title = if(currentMedication == null) "Medikament hinzufügen" else "Medication bearbeiten",
        topNavigationButtons = {},
    ) {

        // NAME TEXT FIELD
        FormTextFieldRow(
            inputValue = medicationName.value,
            onValueChange = {
                medicationName.value = it
                newOrUpdatedTimer = newOrUpdatedTimer.copy(medication = newOrUpdatedTimer.medication.copy(name = it))
            },
            displayText = "Name des Medikaments",
        )

        // DOSAGE TEXT FIELD
        FormTextFieldRow(
            inputValue = medicationDosage.value,
            leadingIcon = Res.drawable.ic_description,
            onValueChange = {
                medicationDosage.value = it
                newOrUpdatedTimer = newOrUpdatedTimer.copy(medication = newOrUpdatedTimer.medication.copy(dosage = it))
            },
            displayText = "Dosis in mg",
        )

        FormIntakeTime(
            medicationId = newOrUpdatedTimer.medication.medicationId,
            intakeDates = newOrUpdatedTimer.intakeTimesWithStatus,
            onValueChange = {
                newOrUpdatedTimer = newOrUpdatedTimer.copy(intakeTimesWithStatus = it)
            }
        )

        FormControlButtons(
            onSave = {
                isNameFocused.value = false
                isDosageFocused.value = false

                focusManager.clearFocus()
                viewModel.insertMedicationWithIntakeDetails(newOrUpdatedTimer)
                navController.navigate(Destination.Medication.route)
            },
            onCancel = {
                isNameFocused.value = false
                isDosageFocused.value = false

                focusManager.clearFocus()
                navController.navigate(Destination.Medication.route)
            }
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}





@Composable
fun IntakeTimeTextField(
    modifier: Modifier = Modifier,
    initialValue: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit = {},
    onRemove: () -> Unit
) {
    // Extrahiere Stunden und Minuten aus dem initialen Wert
    val (initialHour, initialMinute) = remember(initialValue) {
        val parts = initialValue.split(":")
        parts.getOrElse(0) { "00" } to parts.getOrElse(1) { "00" }
    }

    // Verwende State-Variablen für Stunden und Minuten
    var hourInput by remember { mutableStateOf(initialHour) }
    var minuteInput by remember { mutableStateOf(initialMinute) }

    // Aktualisiere den vollständigen Wert bei Änderungen
    LaunchedEffect(hourInput, minuteInput) {
        logging().info {
            "hourInput: $hourInput, minuteInput: $minuteInput"
        }
        onValueChange("$hourInput:$minuteInput")
    }


    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = hourInput,
                onValueChange = {
                    if (it.length <= 2 && it.toIntOrNull() in 0..23) {
                        hourInput = it
                    }
                },
                modifier = Modifier
                    .weight(1f),
                placeholder = {
                    Text(text = placeholder)
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                )
            )

            BodyText(":")

            TextField(
                value = minuteInput,
                onValueChange = {
                    if (it.length <= 2 && it.toIntOrNull() in 0..59) {
                        minuteInput = it
                    }

                },
                modifier = Modifier
                    .weight(1f),
                placeholder = {
                    Text(text = placeholder)
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                )
            )
        }

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .offset(x = 16.dp, y = (-16).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.2f))
                .padding(4.dp)
                .clickableWithRipple { onRemove() }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormIntakeTime(
    medicationId: String,
    intakeDates: List<IntakeTimeWithStatus> = mutableListOf(),
    onValueChange: (List<IntakeTimeWithStatus>) -> Unit = {}
) {
    // Maintain a state-backed copy of the intakeTimes
    var intakeTimeWithStatuses by remember { mutableStateOf(intakeDates) }

    // Sync initial intakeDates when they're passed
    LaunchedEffect(intakeDates) {
        intakeTimeWithStatuses = intakeDates.toMutableList()
    }

    Column {
        FlowRow(
            modifier = Modifier,
            maxItemsInEachRow = 2,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            intakeTimeWithStatuses.forEachIndexed { index, intakeTimeWithStatus ->

                IntakeTimeTextField(
                    modifier = Modifier.width(120.dp),
                    initialValue = intakeTimeWithStatus.intakeTime.intakeTime,
                    onValueChange = { newValue ->
                        // Create a copy of the list, modify the item, and propagate changes
                        val updatedList = intakeTimeWithStatuses.toMutableList()
                        updatedList[index] = intakeTimeWithStatus.copy(
                            intakeTime = intakeTimeWithStatus.intakeTime.copy(intakeTime = newValue)
                        )
                        intakeTimeWithStatuses = updatedList // This will trigger recomposition
                        onValueChange(updatedList) // Notify parent about the change
                    },
                    onRemove = {
                        // Create a new list without the removed item and propagate changes
                        val updatedList = intakeTimeWithStatuses.toMutableList()
                        updatedList.removeAt(index)
                        intakeTimeWithStatuses = updatedList // Trigger recomposition
                        onValueChange(updatedList) // Notify parent about the change
                    }
                )
            }

            AddButton(
                onClick = {
                    // Add a new IntakeTimeWithStatus and propagate the changes
                    val timeZone = TimeZone.currentSystemDefault()
                    val time = Clock.System.now().toLocalDateTime(timeZone)
                    val hour = time.hour.toString().padStart(2, '0')
                    val minute = time.minute.toString().padStart(2, '0')
                    val newIntakeTimeWithStatus = IntakeTimeWithStatus(
                        intakeTime = IntakeTime(
                            intakeTime =  "${hour}:${minute}",
                            medicationId = medicationId
                        ),
                        intakeStatuses = emptyList()
                    )
                    val updatedList = intakeTimeWithStatuses.toMutableList().apply {
                        add(newIntakeTimeWithStatus)
                    }
                    intakeTimeWithStatuses = updatedList // Trigger recomposition
                    onValueChange(updatedList)
                }
            )
        }
    }
}

@Composable
fun AddButton(
    text: String = "Hinzufügen",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .sectionShadow()
            .padding(8.dp)
            .clickableWithRipple {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Remove",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            text = text,
        )
    }
}
