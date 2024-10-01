package ui.screens.authScreens.medication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_grid_2_2
import bauchglueck.composeapp.generated.resources.ic_pills_fill
import data.local.entitiy.MedicationWithIntakeDetails
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import ui.navigations.Destination
import ui.components.theme.ScreenHolder
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.FooterText
import ui.screens.authScreens.medication.components.MedicationHistory
import viewModel.MedicationViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.medication(navController: NavHostController){
    composable(Destination.Medication.route) {
        val viewModel: MedicationViewModel = viewModel<MedicationViewModel>()
        val allMedications by viewModel.medicationsWithIntakeDetailsForToday.collectAsStateWithLifecycle(initialValue = emptyList())
        val allMedicationHistory by viewModel.medicationHistory.collectAsStateWithLifecycle(initialValue = emptyList())
        val viewType = rememberSaveable { mutableStateOf(MediationViewType.History) }

        Box {
            ScreenHolder(
                title = Destination.Medication.title,
                showBackButton = true,
                onNavigate = {
                    navController.navigate(Destination.Home.route)
                },
                optionsRow = {
                    Icon(
                        imageVector = vectorResource(resource = Res.drawable.ic_pills_fill),
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                            .clickableWithRipple { navController.navigate(Destination.AddMedication.route) },
                    )

                    Icon(
                        imageVector = vectorResource(resource = Res.drawable.ic_gear),
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                            .clickableWithRipple { navController.navigate(Destination.Settings.route) },
                    )
                },
            ) {

                // NAVIGATION
                NavigationOverlay(
                    currentViewType = viewType.value
                ) {
                    viewType.value = it
                }

                // TABS
                when (viewType.value) {
                    MediationViewType.Today -> allMedications.forEach { medication ->
                        MedicationCard(
                            modifier = Modifier,
                            medication = medication,
                            onEdit = {
                                navController.navigate(Destination.EditMedication.route)
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "medicationId",
                                    it.medication.medicationId
                                )
                            },
                            onUpdateTakenState = { medicationWithIntakeDetailsForToday ->
                                viewModel.insertMedicationWithIntakeDetails(
                                    MedicationWithIntakeDetails(
                                        medication = medicationWithIntakeDetailsForToday.medication,
                                        intakeTimesWithStatus = medicationWithIntakeDetailsForToday.intakeTimesWithStatus
                                    )
                                )
                            }
                        )
                    }
                    MediationViewType.History -> allMedicationHistory.forEach { medicationHistory ->
                        MedicationHistory(medicationHistory = medicationHistory)
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationOverlay(
    currentViewType: MediationViewType = MediationViewType.Today,
    onTab: (MediationViewType) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MediationViewType.entries.forEach { medicationViewType ->
            val iconTintColor by animateColorAsState(
                targetValue = if (medicationViewType == currentViewType) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(0.9f),
                label = ""
            )

            val backgroundColorTop by animateColorAsState(
                targetValue = if (medicationViewType == currentViewType) MaterialTheme.colorScheme.primary else Color.Gray.copy(0.4f),
                label = ""
            )

            val backgroundColorButton by animateColorAsState(
                targetValue = if (medicationViewType == currentViewType) MaterialTheme.colorScheme.primaryContainer else Color.Gray.copy(0.1f),
                label = ""
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                backgroundColorTop,
                                backgroundColorButton
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .clickableWithRipple { onTab(medicationViewType) },
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = vectorResource(resource = medicationViewType.icon),
                    contentDescription = "",
                    tint = iconTintColor
                )

                FooterText(text = medicationViewType.text)
            }
        }
    }
}

enum class MediationViewType(val icon: DrawableResource, val text: String) {
    Today(icon = Res.drawable.ic_pills_fill, text = "Einnahme"),
    History(icon = Res.drawable.ic_grid_2_2, text = "Verlauf")
}