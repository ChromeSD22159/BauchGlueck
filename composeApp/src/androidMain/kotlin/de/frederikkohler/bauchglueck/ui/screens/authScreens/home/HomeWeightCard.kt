package de.frederikkohler.bauchglueck.ui.screens.authScreens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.ui.components.CardTitle
import de.frederikkohler.bauchglueck.ui.components.HeadCard
import viewModel.DateViewModel
import kotlin.random.Random

@Composable
fun HomeWeightCard(
    title: String = "Gewichtsverlust",
    onNavigate: () -> Unit
) {

    WeightLossCard()
    Spacer(modifier = Modifier.height(16.dp))
    HeadCard(
        title = title,
        onNavigate = { onNavigate() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ROW CONTENT
        }
    }
}

@Composable
fun WeightLossCard(
    dateViewModel: DateViewModel = viewModel()
) {
    val lastSevenWeeks by dateViewModel.previousSevenWeeks.collectAsStateWithLifecycle()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFA726)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CardTitle("Gewichtsverlust")

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Trending Up",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //Chart
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                lastSevenWeeks.forEach { week ->
                    val firstWeekDay = week.first.dayOfMonth
                    // Chart Bar Item
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(Random.nextInt(15, 100).dp)
                                .width(16.dp)
                                .background(Color.White, shape = RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val firstWeekday = firstWeekDay.toString().padStart(2, '0')
                        val lastWeekday = week.second.dayOfMonth.toString().padStart(2, '0')
                        val dateRange = "$firstWeekday - ${lastWeekday}.${week.second.monthNumber}"

                        Text(
                            text = dateRange,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "In einigen Tagen siehst du hier deine Statistiken",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}