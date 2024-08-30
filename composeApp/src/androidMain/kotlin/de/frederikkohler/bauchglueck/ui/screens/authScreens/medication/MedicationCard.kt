package de.frederikkohler.bauchglueck.ui.screens.authScreens.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.clickableWithRipple
import de.frederikkohler.bauchglueck.ui.theme.AppTheme

@Composable
fun MedicationCard(
    medication: MedicationTest,
    onEdit: (IntakeTimes) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_pills_fill),
                contentDescription = "Medication Icon"
            )

            Text(text = medication.name)
        }

        Spacer(modifier = Modifier.weight(1f))

        medication.intakeTimes.forEach { intakeTime ->
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            if (intakeTime.token) Color.Gray.copy(alpha = 0.2f) else Color.Gray.copy(
                                alpha = 0.9f
                            ),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .clickableWithRipple {
                            onEdit(intakeTime.copy(token = !intakeTime.token))
                        }
                )

                Text(
                    text = intakeTime.intakeTime.padStart(5, '0'),
                    color = if (intakeTime.token) Color.Gray.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.6f),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        }
    }
}

data class MedicationTest(
    val id: Int = 0,
    val name: String = "Ibuprofen",
    val dosage:String  = "200mg",
    val intakeTimes:List<IntakeTimes> = emptyList(),
    val isDeleted:Boolean = false
)

data class IntakeTimes(
    val intakeTime: String = "8:00",
    val token: Boolean = false
)



@Preview(showBackground = true)
@Composable
fun MedicationCardPreview() {
    val medications: List<MedicationTest> = listOf(
        MedicationTest(
            name = "Ibuprofen",
            dosage = "200mg",
            intakeTimes = listOf(IntakeTimes("8:00", true), IntakeTimes("12:00", false), IntakeTimes("16:00", true)),
            isDeleted = false
        ),
        MedicationTest(
            name = "Ibuprofen",
            dosage = "200mg",
            intakeTimes = listOf(IntakeTimes("8:00", true), IntakeTimes("12:00", false), IntakeTimes("16:00", true)),
            isDeleted = false
        )
    )

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            medications.forEach { medication ->
                MedicationCard(medication)
            }
        }
    }
}