package ui.screens.authScreens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import util.DateRepository
import util.toDateString

@Composable
fun NextMedicationCard(
    navController: NavHostController,
    medications: List<MedicationWithIntakeDetailsForToday> = emptyList(),
    medicationListNotTakenToday: List<MedicationWithIntakeDetailsForToday> = emptyList()
) {
    Section(
        sectionModifier = Modifier
            .padding(horizontal = 10.dp)
            .clickableWithRipple { navController.navigate(Destination.Medication.route) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {

            HeadlineText(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                size = 16.sp,
                weight = FontWeight.Medium,
                text = "Medikamente für heute ${DateRepository.today.toDateString()}"
            )

            val nextMedication = medicationListNotTakenToday
                .flatMap { medication ->
                    medication.intakeTimesWithStatus.map { intakeTimeWithStatus ->
                        Pair(medication.medication.name, intakeTimeWithStatus)
                    }
                }
                .filter { (_, intakeTimeWithStatus) ->
                    // Filter only those intake times that are not yet taken
                    intakeTimeWithStatus.intakeStatuses.none { it.isTaken }
                }
                .minByOrNull { (_, intakeTimeWithStatus) ->
                    // Find the minimum (next) intake time
                    val (hour, minute) = intakeTimeWithStatus.intakeTime.intakeTime.split(":")
                        .map { it.toInt() }
                    hour * 60 + minute // Convert time to total minutes
                }

            if (nextMedication != null) {
                val (nextMedicationName, nextIntakeTimeWithStatus) = nextMedication
                val nextIntakeTime = nextIntakeTimeWithStatus.intakeTime.intakeTime

                FooterText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    size = 12.sp,
                    text = "Nächstes Medikament für heute:\n ${
                        nextMedicationName.replace(
                            " ",
                            ""
                        )
                    } um $nextIntakeTime Uhr"
                )
            } else {
                FooterText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    size = 12.sp,
                    text = if (medications.isEmpty()) "Du hast heut keine Medikamente zum einnehmen" else "Du hast alle Medikamente eingenommen!"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            medications.forEach { medication ->
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    BodyText(
                        modifier = Modifier.weight(1f),
                        text = medication.medication.name,
                        weight = FontWeight.Medium
                    )


                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            alignment = Alignment.End
                        ),
                    ) {
                        medication.intakeTimesWithStatus.forEach { intakeTime ->
                            FooterText(text = "${intakeTime.intakeTime.intakeTime} Uhr")
                        }
                    }
                }
            }
        }
    }
}