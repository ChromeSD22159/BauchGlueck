package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.DateConverter

@Composable
fun TimerCard(
    timer: CountdownTimer
) {

    var remainingTime by remember {
        mutableLongStateOf(timer.duration)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
            .padding(16.dp)
            .heightIn(max = 100.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
                //color = MaterialTheme.colorScheme.onTertiaryContainer,
                text = DateConverter().formatTimeToMMSS(remainingTime)
            )
            Text(
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                // color = MaterialTheme.colorScheme.onSecondaryContainer,
                text = timer.name
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(2f)
        ) {
            TimerCircle(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                totalTime = timer.duration * 1000,
                handleColor = MaterialTheme.colorScheme.secondary, // Dot
                inactiveBarColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.25f), // Circle
                activeBarColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                remainingTime = { remainingTime = it}
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TimerCardPreview() {
    AppTheme {
        val timer = CountdownTimer(
            name = "Test",
            duration = 2000L
        )
        TimerCard(timer = timer)
    }
}