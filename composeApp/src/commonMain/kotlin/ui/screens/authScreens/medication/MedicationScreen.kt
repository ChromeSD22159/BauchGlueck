package ui.screens.authScreens.medication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_pills_fill
import data.local.entitiy.MedicationWithIntakeDetails
import org.koin.androidx.compose.koinViewModel
import ui.components.BackScaffold
import ui.components.RoundImageButton
import ui.navigations.Destination
import org.lighthousegames.logging.logging
import viewModel.MedicationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationScreen(
    navController: NavHostController,
    viewModel: MedicationViewModel = koinViewModel<MedicationViewModel>()
) {
    val allMedications by viewModel.medicationsWithIntakeDetailsForToday.collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(allMedications) {
        if(allMedications.isNotEmpty()) {
            logging().info { "UI: Fetched ${allMedications.size} medications in UI without Lifecycle." }
        }
    }

    BackScaffold(
        title = Destination.Medication.title,
        navController = navController,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = Res.drawable.ic_pills_fill,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddMedication.route)
                    }
                )
            }

            Row {
                RoundImageButton(
                    icon = Res.drawable.ic_gear,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {

        allMedications.forEach { medication ->
            MedicationCard(
                modifier = Modifier,
                medication = medication,
                onEdit = {
                    navController.navigate(Destination.EditMedication.route)
                    navController.currentBackStackEntry?.savedStateHandle?.set("medicationId", it.medication.medicationId)
                },
                onUpdateTakenState = { medicationWithIntakeDetailsForToday  ->
                    viewModel.insertMedicationWithIntakeDetails(
                        MedicationWithIntakeDetails(
                            medication = medicationWithIntakeDetailsForToday.medication,
                            intakeTimesWithStatus = medicationWithIntakeDetailsForToday.intakeTimesWithStatus
                        )
                    )
                }
            )
        }

    }
}