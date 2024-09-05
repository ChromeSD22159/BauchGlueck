package de.frederikkohler.bauchglueck.ui.screens.authScreens.medication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import data.local.entitiy.MedicationWithIntakeDetails
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.koinViewModel
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import de.frederikkohler.bauchglueck.ui.components.clickableWithRipple
import de.frederikkohler.bauchglueck.ui.navigations.Destination
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
                    icon = R.drawable.ic_pills_fill,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddMedication.route)
                    }
                )
            }

            Row {
                RoundImageButton(
                    icon = R.drawable.ic_gear,
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