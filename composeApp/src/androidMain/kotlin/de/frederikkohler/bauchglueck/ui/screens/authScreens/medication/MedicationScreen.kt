package de.frederikkohler.bauchglueck.ui.screens.authScreens.medication

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.navigations.Destination

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicationScreen(
    navController: NavHostController
) {
    val medications: List<MedicationTest> = listOf(
        MedicationTest(
            id = 1,
            name = "Ibuprofen",
            dosage = "200mg",
            intakeTimes = listOf(IntakeTimes("8:00", true), IntakeTimes("12:00", false), IntakeTimes("16:00", true)),
            isDeleted = false
        ),
        MedicationTest(
            id = 2,
            name = "Ibuprofen",
            dosage = "200mg",
            intakeTimes = listOf(IntakeTimes("8:00", true), IntakeTimes("12:00", false), IntakeTimes("16:00", true)),
            isDeleted = false
        )
    )

    BackScaffold(
        title = Destination.Medication.title,
        navController = navController
    ) {

        medications.forEach { medication ->
            MedicationCard(medication) { updatedMedication ->
                val medicationToUpdate = medications.find { medicationTest -> medicationTest.id == medication.id  }
                medicationToUpdate?.let {
                    it.intakeTimes.find { intakeItem -> intakeItem.intakeTime == updatedMedication.intakeTime }?.let {
                        it.copy(token = updatedMedication.token)
                    }
                }
            }
        }

    }
}