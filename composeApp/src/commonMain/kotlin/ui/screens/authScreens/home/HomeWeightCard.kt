package ui.screens.authScreens.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_arrow_down_right_fill
import bauchglueck.composeapp.generated.resources.ic_arrow_up_right_fill
import data.model.WeeklyAverage
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.clickableWithRipple
import ui.components.extentions.sectionShadow
import ui.components.theme.text.FooterText
import ui.navigations.Destination

@Composable
fun WeightCardChartCard(
    hasValidData: Boolean,
    weeklyAverage: List<WeeklyAverage>,
    navController: NavHostController,
) {
    if (hasValidData) {
        HomeWeightCard(weeklyAverage) { navController.navigate(Destination.Weight.route) }
    } else {
        HomeWeightMockCard { navController.navigate(Destination.Weight.route) }
    }
}

@Composable
fun HomeWeightCard(
    data: List<WeeklyAverage>,
    horizontalSpacing: Dp = 10.dp,
    onNavigate: () -> Unit
) {
    val brush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    )

    val hasValidData = data.any { it.avgValue > 0.0 }

    var entries by remember { mutableStateOf(listOf<WeeklyAverage>()) }

    LaunchedEffect(key1 = data) {
        entries = data
    }

    Column(
        modifier = Modifier
            .padding(horizontal = horizontalSpacing)
            .sectionShadow()
            .clickableWithRipple { onNavigate() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = brush)
        ) {
            Column {
                //Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Chart(hasValidData = hasValidData, entries = entries)

                    Trend(isAscending = entries.calculateWeightTrend())
                }

                FootRow("Deine Gewichtsverlauf der letzte Wochen")
            }
        }
    }
}

@Composable
fun HomeWeightMockCard(
    horizontalSpacing: Dp = 10.dp,
    onNavigate: () -> Unit
) {
    val hasValidData = false

    val mockList = List(7) { i ->
        val date = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val newDate = date.minus(value = (i * 7).toLong(), unit = DateTimeUnit.DAY)
        WeeklyAverage(
            week = "${newDate.dayOfMonth.toString().padStart(2, '0')}.${newDate.monthNumber.toString().padStart(2, '0')}",
            avgValue = (50..100).random().toDouble()
        )
    }

    val entries by remember { mutableStateOf(mockList) }

    Column(
        modifier = Modifier
            .padding(horizontal = horizontalSpacing)
            .sectionShadow()
            .clickableWithRipple { onNavigate() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Chart(hasValidData = hasValidData, entries = entries)

            if(!hasValidData) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.0f)
                                )
                            )
                        )
                )
            }

            Trend(isAscending = true)
        }

        FootRow("In einigen Tagen siehst du hier deine Statistik")
    }
}

@Composable
fun Chart(
    calendarItemMaxHeight: Int = 100,
    hasValidData: Boolean,
    entries: List<WeeklyAverage>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .alpha(if (hasValidData) 1f else 0.5f)
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        val maxHeight = entries.maxOfOrNull { it.avgValue } ?: calendarItemMaxHeight.toDouble()

        entries.forEachIndexed { index, week ->

            var animatedHeight by remember { mutableDoubleStateOf(0.0) }

            // Start the animation with delay for each item
            val percent = (week.avgValue / maxHeight) * 100
            LaunchedEffect(key1 = index) {
                delay(index * 75L)
                animatedHeight = percent
            }

            val height by animateDpAsState(targetValue = animatedHeight.dp, label = "Bar height")

            // Chart Bar Item
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .height((calendarItemMaxHeight * 2).dp)
                    .padding(top = 16.dp)
            ) {

                // Chart Item
                Box(
                    modifier = Modifier
                        .height(height)
                        .width(16.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(if (hasValidData) 1f else 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = week.week,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    modifier = Modifier.rotate((-90).toFloat())
                )
            }
        }
    }
}

@Composable
fun FootRow(
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        FooterText(
            text = text,
            modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 8.dp)
        )
    }
}

@Composable
fun Trend(
    isAscending: Boolean = true
) {
    Row(
        Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = vectorResource(resource = if(isAscending) Res.drawable.ic_arrow_up_right_fill else Res.drawable.ic_arrow_down_right_fill),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = ""
        )

        FooterText(text = if(isAscending) "Aufsteigender Trend" else "Absteigender Trend")
    }
}

fun List<WeeklyAverage>.calculateWeightTrend(): Boolean {
    // Sortiere die Daten nach Woche, falls sie nicht bereits sortiert sind
    val sortedData = this.sortedBy { it.week }

    // Prüfe, ob genügend Daten vorhanden sind (mindestens 14 Wochen)
    if (sortedData.size < 14) return false

    // Nimm die letzten 7 Wochen
    val last7Weeks = sortedData.takeLast(7)
    // Nimm die vorherigen 7 Wochen (8-14)
    val previous7Weeks = sortedData.takeLast(14).take(7)

    // Berechne den Durchschnitt für die letzten 7 Wochen
    val last7Average = last7Weeks.map { it.avgValue }.average()

    // Berechne den Durchschnitt für die vorherigen 7 Wochen
    val previous7Average = previous7Weeks.map { it.avgValue }.average()

    // Vergleiche die beiden Durchschnitte
    return last7Average < previous7Average
}