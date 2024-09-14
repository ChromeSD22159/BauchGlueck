package ui.screens.authScreens.home

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.model.DailyAverage
import de.frederikkohler.bauchglueck.R
import ui.components.HeadCard

@Composable
fun HomeWeightCard(
    data: List<DailyAverage>,
    title: String = "Gewichtsverlust",
    onNavigate: () -> Unit
) {
    WeightLossCard(
        title = title,
        data = data,
        onNavigate = onNavigate
    )
}

@Composable
fun WeightLossCard(
    title: String,
    data: List<DailyAverage>,
    onNavigate: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f), //Color(0xFFFFA726)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
    ) {
        HeadCard(
            icon = R.drawable.ic_chart,
            title = title,
            onNavigate = { onNavigate() }
        ) {
            //Chart
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                val maxHeight = data.maxOfOrNull { it.avgValue } ?: 100.0

                data.forEach { week ->

                    // Chart Bar Item
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp)
                    ) {

                        // Chart Item
                        val percent = (week.avgValue / maxHeight) * 100
                        Box(
                            modifier = Modifier
                                .height(percent.dp)
                                .width(16.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = week.date,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            modifier = Modifier.rotate((-90).toFloat())
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
                Text(
                    text = "In einigen Tagen siehst du hier deine Statistiken",
                    //color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 0.dp, start = 8.dp, end = 8.dp)
                )
            }
        }
    }
}