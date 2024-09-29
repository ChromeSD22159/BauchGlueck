package ui.screens.authScreens.medication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_pills_fill
import kotlinx.datetime.Clock
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.text.FooterText
import util.Weekday
import util.toEpochMillis
import data.model.medication.MedicationHistory
import ui.components.theme.text.HeadlineText
import util.startEndTodayIn

@Composable
fun MedicationHistory(
    modifier: Modifier = Modifier,
    medicationHistory: MedicationHistory,
) {
    val itemHeight = 16.dp
    val spacing = (itemHeight.value / 6).dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_pills_fill),
                contentDescription = "Medication Icon"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                HeadlineText(
                    text = medicationHistory.medicationName,
                    size = 16.sp
                )

                FooterText(
                    text = medicationHistory.dosage
                )
            }
        }

        // GRID
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            val medicationDays = medicationHistory.medicationWeek.map { it.reversed() }.flatten()

            LazyHorizontalGrid(
                rows = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((7 * itemHeight.value).dp + (6 * spacing.value).dp),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalArrangement = Arrangement.spacedBy(spacing),
                reverseLayout = false,

            ) {
                dayLegend(isFirstLegend = true)

                items(medicationDays.size) { index ->
                    val medicationForDay = medicationDays[index]
                    ColorBox(
                        height = itemHeight,
                        percentage = medicationForDay.percentage,
                        isInFuture = Clock.System.now().toEpochMilliseconds() < medicationForDay.date.startEndTodayIn().start,
                        isToday = Clock.System.now().toEpochMilliseconds() > medicationForDay.date.startEndTodayIn().start && Clock.System.now().toEpochMilliseconds() < medicationForDay.date.startEndTodayIn().end
                    )
                }

                dayLegend(isFirstLegend = false)
            }
        }

        // LEGEND
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FooterText(text = "Legende:")

            medicationHistory.legend.forEach {
                ColorBox(
                    height = itemHeight,
                    percentage = it,
                    isInFuture = false,
                    isToday = false
                )
            }
        }
    }
}

fun LazyGridScope.dayLegend(
    itemHeight: Dp = 16.dp,
    isFirstLegend: Boolean = false
) {
    items(Weekday.entries.size) {
        Row(
            modifier = Modifier
                .height(itemHeight),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterText(
                modifier = Modifier.padding(start = if (isFirstLegend) 0.dp else 8.dp, end = if (isFirstLegend) 8.dp else 0.dp),
                text = Weekday.fromInt(it).displayName.substring(0..1),
                textAlign = TextAlign.Center
            )
        }
    }
}