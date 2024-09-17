package ui.screens.authScreens.medication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_pills_fill
import data.local.entitiy.MedicationWithIntakeDetails
import ui.navigations.Destination
import org.lighthousegames.logging.logging
import ui.components.theme.button.IconButton
import ui.components.theme.ScreenHolder
import viewModel.MedicationViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.medication(navController: NavHostController){
    composable(Destination.Medication.route) {
        val viewModel: MedicationViewModel = viewModel<MedicationViewModel>()
        val allMedications by viewModel.medicationsWithIntakeDetailsForToday.collectAsStateWithLifecycle(initialValue = emptyList())

        LaunchedEffect(allMedications) {
            if(allMedications.isNotEmpty()) {
                logging().info { "UI: Fetched ${allMedications.size} medications in UI without Lifecycle." }
            }
        }

        ScreenHolder(
            title = Destination.Medication.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_pills_fill,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.AddMedication.route)
                }

                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            },
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
}