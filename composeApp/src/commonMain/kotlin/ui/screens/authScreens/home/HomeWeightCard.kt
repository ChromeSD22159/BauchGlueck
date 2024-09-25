package ui.screens.authScreens.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_chart
import data.model.WeeklyAverage
import kotlinx.coroutines.delay
import org.lighthousegames.logging.logging
import ui.components.theme.HeadCard
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.sectionShadow
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.screens.authScreens.searchRecipes.getSize

@Composable
fun HomeWeightCard(
    data: List<WeeklyAverage>,
    title: String = "Gewichtsverlust",
    horizontalSpacing: Dp = 10.dp,
    calendarItemMaxHeight: Int = 100,
    onNavigate: () -> Unit
) {

    val hasData = data.any { it.avgValue > 0.0 }

    logging().info { "HomeWeightCard: $hasData" }
    logging().info { "HomeWeightCard: $data" }

    val generateMockData: List<WeeklyAverage> = if (data.isNotEmpty()) {
        List(7) { index ->
            WeeklyAverage(
                week = data[index].week,
                avgValue = (50..100).random().toDouble()
            )
        }
    } else {
        List(7) { index ->
            WeeklyAverage(
                week = "KW ${index + 1}",
                avgValue = (50..100).random().toDouble()
            )
        }
    }

    val list = if (hasData) data else generateMockData

    Column(
        modifier = Modifier
            .padding(horizontal = horizontalSpacing)
            .sectionShadow()
            .clickableWithRipple { onNavigate() }
    ) {
        //Chart
        Box {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .alpha(if(hasData) 1f else 0.5f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                val maxHeight = data.maxOfOrNull { it.avgValue } ?: calendarItemMaxHeight.toDouble()

                list.forEachIndexed { index, week ->

                    var animatedHeight by remember { mutableDoubleStateOf(0.0) }

                    // Start the animation with delay for each item
                    val percent = (week.avgValue / maxHeight) * 100
                    LaunchedEffect(key1 = index) {
                        delay(index * 100L) // Delay each item by 100ms
                        animatedHeight = percent
                    }


                    val height by animateDpAsState(targetValue = animatedHeight.dp, label = "Bar height")

                    // Chart Bar Item
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp)
                    ) {

                        // Chart Item

                        Box(contentAlignment = Alignment.BottomCenter) {
                            Box(
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(16.dp)
                                    .background(
                                        Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )

                            Box(
                                modifier = Modifier
                                    .height(height)
                                    .width(16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(if (hasData) 1f else 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = week.week,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            modifier = Modifier.rotate((-90).toFloat())
                        )
                    }
                }
            }

            if(!hasData) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.0f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    HeadlineText(
                        color = MaterialTheme.colorScheme.onPrimary,
                        text = "In einigen Tagen siehst du\nhier deine Statistik!",
                        size = 16.sp
                    )
                }
            }

        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            FooterText(
                text = if (hasData) "Deine Gewichtsverlauf der letzte Wochen" else "In einigen Tagen siehst du hier deine Statistik",
                modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}