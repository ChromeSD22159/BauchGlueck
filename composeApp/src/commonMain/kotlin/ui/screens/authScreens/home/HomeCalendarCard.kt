package ui.screens.authScreens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.R
import ui.components.HeadCard
import viewModel.DateViewModel

@Composable
fun HomeCalendarCard(
    dateViewModel: DateViewModel = viewModel(),
    onNavigate: () -> Unit
) {
    val dates by dateViewModel.nextSevenDays.collectAsStateWithLifecycle()

    HeadCard(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        icon = R.drawable.icon_calendar,
        title = "Kalender",
        onNavigate = { onNavigate() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dates.forEach {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .height(30.dp)
                        .width(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        text = "${it.dayOfMonth}"
                    )
                }
            }
        }
    }
}

