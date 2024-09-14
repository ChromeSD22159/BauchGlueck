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
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavHostController
import data.local.entitiy.IntakeTime
import data.local.entitiy.IntakeTimeWithStatus
import data.local.entitiy.Medication
import data.local.entitiy.MedicationWithIntakeDetails
import org.koin.androidx.compose.koinViewModel
import ui.components.FormScreens.FormControlButtons
import ui.components.FormScreens.FormTextFieldRow
import ui.components.ItemOverLayScaffold
import ui.components.clickableWithRipple
import ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import util.UUID
import viewModel.MedicationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEditMedicationScreen(
    navController: NavHostController,
    currentMedication: String? = null
) {
    val viewModel = koinViewModel<MedicationViewModel>()

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
        topNavigationButtons = {
        IconButton(onClick = {
            focusManager.clearFocus()

            navController.navigate(Destination.Medication.route)
        }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Localized description"
            )
        }
    },
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
    Box(
        modifier = modifier
    ) {
        TextField(
            value = initialValue,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
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

            Row(
                modifier = Modifier
                    .background(
                        color = Color.Gray.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .clickableWithRipple {
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
                        onValueChange(updatedList) // Notify parent about the change
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Remove",
                    tint = Color.Red,
                    modifier = Modifier
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    text = "Hinzufügen",
                )
            }
        }
    }
}
