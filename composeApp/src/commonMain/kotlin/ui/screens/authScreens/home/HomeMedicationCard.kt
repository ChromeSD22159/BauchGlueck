package ui.screens.authScreens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import ui.components.theme.SliderItemAddCard
import ui.components.theme.clickableWithRipple
import ui.components.theme.sectionShadow
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import viewModel.MedicationViewModel

@Composable
fun HomeMedicationCard(
    horizontalSpacing: Dp = 10.dp,
    onNavigate: (Destination) -> Unit,
    height: Dp = 80.dp,
    medicationViewModel: MedicationViewModel
) {
    val medications by medicationViewModel.medicationsWithIntakeDetailsForToday.collectAsStateWithLifecycle(initialValue = emptyList())

    LazyHorizontalGrid(
        modifier = Modifier.height(height + 10.dp),
        rows = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(horizontalSpacing),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(medications.size, key = { it }) {
            MedCard(medications[it], height = height) { onNavigate(Destination.Medication) }
        }

        item {
            SliderItemAddCard(Destination.AddMedication) {
                onNavigate(it)
            }
        }
    }
}

@Composable
fun MedCard(
    medication: MedicationWithIntakeDetailsForToday,
    height: Dp = 80.dp,
    onNavigate: (Destination) -> Unit
) {
    val intakesTimes = medication.intakeTimesWithStatus.size

    val intakes = medication.intakeTimesWithStatus.flatMap { status ->
        status.intakeStatuses.filter { it.isTaken }
    }.size

    Box(
        modifier = Modifier
            .height(height)
            .width(100.dp)
            .sectionShadow()
            .clickableWithRipple {
                onNavigate(Destination.Medication)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            HeadlineText(
                text = medication.medication.name,
                size = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            FooterText(
                text = "${intakes}/${intakesTimes} eingenommen.",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
