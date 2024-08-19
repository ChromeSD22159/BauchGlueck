package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import kotlinx.datetime.Clock
import util.DateConverter

@Composable
fun TimerCard(timer: CountdownTimer) {

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    //color = MaterialTheme.colorScheme.onTertiaryContainer,
                    text = DateConverter().formatTimeToMMSS(timer.duration)
                )
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    // color = MaterialTheme.colorScheme.onSecondaryContainer,
                    text = timer.name
                )
            }

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {

                var endTime = if (timer.endDate != null) {
                    timer.endDate!! - Clock.System.now().toEpochMilliseconds()
                } else {
                    0
                }

                CircularProgressIndicator(
                    progress = { (timer.duration - endTime) / timer.duration.toFloat() },
                    modifier = Modifier.size(80.dp),
                    //color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 8.dp,
                )

                Icon(
                    //tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_stromach),
                    contentDescription = ""
                )
            }
        }
    }
}




@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun TimerScreenPreview() {
    val timer = CountdownTimer(
        timerId = "123",
        userId = "123",
        name = "Test",
        duration = 60,
        startDate = Clock.System.now().toEpochMilliseconds(),
        endDate = Clock.System.now().toEpochMilliseconds(),
        timerState = "Test",
        showActivity = true
    )
    AppTheme {
        TimerCard(timer)
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
fun TimerScreenPreviewLight() {
    val timer = CountdownTimer(
        timerId = "123",
        userId = "123",
        name = "Test",
        duration = 60,
        startDate = Clock.System.now().toEpochMilliseconds(),
        endDate = Clock.System.now().toEpochMilliseconds(),
        timerState = "Test",
        showActivity = true
    )
    AppTheme {
        TimerCard(timer)
    }
}