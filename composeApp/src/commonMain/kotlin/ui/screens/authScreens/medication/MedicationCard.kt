package ui.screens.authScreens.medication

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_pills_fill
import data.local.entitiy.IntakeStatus
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import ui.components.theme.clickableWithRipple
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.vectorResource
import util.UUID

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
                imageVector = vectorResource(Res.drawable.ic_pills_fill),
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                //ColoredCheckbox(isTaken)
                CHECK(
                    modifier = Modifier
                        .clickableWithRipple {
                            if (thisStatus == null) {
                                // If there's no status, create a new one and mark it as taken
                                val newList = intakeTimeWithStatus.intakeStatuses + IntakeStatus(
                                    intakeStatusId = UUID.randomUUID(),
                                    intakeTimeId = intakeTime.intakeTimeId,
                                    isTaken = true,
                                    updatedAtOnDevice = Clock.System
                                        .now()
                                        .toEpochMilliseconds()
                                )
                                intakeTimeWithStatus.intakeStatuses = newList
                                isTaken = true
                            } else {
                                if (isTaken) {
                                    thisStatus.isTaken = !thisStatus.isTaken
                                    thisStatus.updatedAtOnDevice = Clock.System
                                        .now()
                                        .toEpochMilliseconds()
                                    isTaken = thisStatus.isTaken
                                } else {
                                    thisStatus.isTaken = !thisStatus.isTaken
                                    thisStatus.updatedAtOnDevice = Clock.System
                                        .now()
                                        .toEpochMilliseconds()
                                    isTaken = thisStatus.isTaken
                                }

                            }
                            onUpdateTakenState(medication)
                        },
                    isTaken = isTaken
                )

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

@Composable
fun CHECK(
    modifier: Modifier = Modifier,
    isTaken: Boolean
) {
    val col = MaterialTheme.colorScheme.primary
    val brush = Brush.linearGradient(
        listOf(
            if(isTaken) col.copy(alpha = 1.0f) else col.copy(alpha = 0.5f),
            if(isTaken) col.copy(alpha = 0.8f) else col.copy(alpha = 0.3f),
        ),
        start = Offset(100f, 0f),
        end = Offset(0f, 100f)
    )
    Box(
        modifier = modifier
            .padding(10.dp)
            .border(
                width = 3.dp,
                brush = brush,
                shape = CircleShape
            )
    ){
        Box( // 52
            modifier = Modifier
                .size(40.dp)
                .padding(6.dp)
                .background(
                    brush = brush,
                    shape = CircleShape
                )
        )
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




