package de.frederikkohler.bauchglueck.ui.screens.authScreens.medication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.local.entitiy.IntakeStatus
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.clickableWithRipple
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import kotlinx.datetime.Clock
import util.generateDeviceId

@Composable
fun MedicationCard(
    modifier: Modifier = Modifier,
    medication: MedicationWithIntakeDetailsForToday,
    onEdit: (MedicationWithIntakeDetailsForToday) -> Unit = {},
    onUpdateTakenState: (MedicationWithIntakeDetailsForToday) -> Unit = { _-> },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .clickableWithRipple {
                    onEdit(medication)
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_pills_fill),
                contentDescription = "Medication Icon"
            )

            Column {
                Text(
                    text = medication.medication.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )

                Text(
                    text = medication.medication.dosage,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        medication.intakeTimesWithStatus.forEach { intakeTimeWithStatus ->
            val intakeTime = intakeTimeWithStatus.intakeTime
            // Check if any intake status exists for this intake time
            val thisStatus = intakeTimeWithStatus.intakeStatuses.firstOrNull { it.intakeTimeId == intakeTime.intakeTimeId }
            var isTaken by remember { mutableStateOf(thisStatus?.isTaken ?: false) }

            // CHECKFIELD
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .clickableWithRipple {
                        if (thisStatus == null) {
                            // If there's no status, create a new one and mark it as taken
                            val newList = intakeTimeWithStatus.intakeStatuses + IntakeStatus(
                                intakeStatusId = generateDeviceId(),
                                intakeTimeId = intakeTime.intakeTimeId,
                                isTaken = true,
                                updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
                            )
                            intakeTimeWithStatus.intakeStatuses = newList
                            isTaken = true
                        } else {
                            if(isTaken) {
                                thisStatus.isTaken = !thisStatus.isTaken
                                thisStatus.updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
                                isTaken = thisStatus.isTaken
                            } else {
                                thisStatus.isTaken = !thisStatus.isTaken
                                thisStatus.updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
                                isTaken = thisStatus.isTaken
                            }

                        }
                        onUpdateTakenState(medication)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ColoredCheckbox(isTaken)

                // DATETIME
                Text(
                    text = intakeTime.intakeTime.padStart(5, '0'),
                    color = if (isTaken) Color.Gray.copy(alpha = 0.9f) else Color.Gray.copy(alpha = 0.6f),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationCardPreview() {
    AppTheme {
        ColoredCheckbox()
    }
}

@Composable
fun ColoredCheckbox(
    isTaken: Boolean? = false
){
    val animatedColor by animateColorAsState(
        if (isTaken == true) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        label = "color"
    )

    val animatedScale by animateFloatAsState(
        if (isTaken == true) {
            1.0.toFloat()
        } else {
            0.95.toFloat()
        },
        label = "padding"
    )

    Box(
        modifier = Modifier
            .size(25.dp)
            .scale(animatedScale)
            .clip(RoundedCornerShape(5.dp))
            .drawBehind {
                drawRect(animatedColor)
            }
    )
}

