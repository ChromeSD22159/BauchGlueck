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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.icon_calendar
import ui.components.theme.HeadCard
import ui.components.theme.text.FooterText
import viewModel.DateViewModel

@Composable
fun HomeCalendarCard(
    horizontalSpacing: Dp = 10.dp,
    dateViewModel: DateViewModel = viewModel(),
    onNavigate: () -> Unit
) {
    val dates by dateViewModel.nextSevenDays.collectAsStateWithLifecycle()

    HeadCard(
        modifier = Modifier.padding(horizontal = horizontalSpacing),
        icon = Res.drawable.icon_calendar,
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
                    FooterText(
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary,
                        text = "${it.dayOfMonth}"
                    )
                }
            }
        }
    }

}

