package ui.screens.authScreens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import data.local.entitiy.CountdownTimer
import org.jetbrains.compose.resources.vectorResource
import ui.components.CardTitle
import ui.components.clickableWithRipple
import ui.navigations.Destination
import util.DateConverter

@Composable
fun HomeTimerCard(
    timers: List<CountdownTimer>,
    title: String = "Timer",
    onNavigate: (Destination) -> Unit
) {

    Column {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_add_timer),
                contentDescription = "icon",
                modifier = Modifier
                    .size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            CardTitle("Timer")

            Spacer(modifier = Modifier.weight(1f))
        }

        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(2.dp))
            timers.forEach { countDownTimer ->
                Column(
                    modifier = Modifier
                        .height(80.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(6.dp).clickableWithRipple {
                            onNavigate(Destination.Timer)
                        },
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        modifier = Modifier.offset(y = (10).dp),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 18.sp),
                        text =  DateConverter().formatTimeToMMSS(countDownTimer.duration)
                    )

                    Text(
                        modifier = Modifier.offset(y = (-10).dp),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 10.sp),
                        text = countDownTimer.name
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clickableWithRipple {
                       onNavigate(Destination.AddTimer)
                    }
                    .height(80.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "icon",
                    modifier = Modifier
                        .size(75.dp)
                )
            }

            Spacer(Modifier.width(10.dp))
        }
    }
}
