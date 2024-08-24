package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.ui.components.DropDownMenu
import de.frederikkohler.bauchglueck.ui.components.DropdownMenuRow
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import util.DateConverter

@Composable
fun TimerCard(
    timer: CountdownTimer,
    onTimerUpdate: (CountdownTimer) -> Unit = {},
    onDelete: (CountdownTimer) -> Unit = {}
) {
    var rowSize by remember { mutableStateOf(IntSize.Zero) }
    var remainingTime by remember {
        mutableLongStateOf(timer.duration)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { rowSize = it }
            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .padding(16.dp)
            .heightIn(max = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                modifier = Modifier
                    .width((rowSize.width / 3).dp)
                    .height(25.dp),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
                text = DateConverter().formatTimeToMMSS(remainingTime)
            )
            Text(
                modifier = Modifier
                    .width((rowSize.width / 3).dp)
                    .height(20.dp),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
                text = timer.name
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top,
        ) {

            TimerCircle(
                modifier = Modifier.size(100.dp),
                countdownTimer = timer,
                handleColor = MaterialTheme.colorScheme.secondary, // Dot
                inactiveBarColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.25f), // Circle
                activeBarColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                remainingTime = { remainingTime = it},
                onTimerUpdate = { onTimerUpdate(it) }
            )

            DropDownMenu(
                dropDownOptions = listOf(
                    DropdownMenuRow(
                        text = "Edit",
                        onClick = {   },
                        leadingIcon = Icons.Outlined.Edit
                    ),
                    DropdownMenuRow(
                        text ="Delete",
                        onClick = { onDelete(timer) },
                        leadingIcon = Icons.Outlined.Delete
                    )
                )
            )
        }
    }
}

@Preview(
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