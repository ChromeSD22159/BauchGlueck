package de.frederikkohler.bauchglueck.ui.screens.authScreens.timer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import data.local.entitiy.CountdownTimer
import de.frederikkohler.bauchglueck.ui.components.ItemOverLayScaffold
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import kotlinx.datetime.Clock
import model.countdownTimer.TimerState
import navigation.Screens
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import util.toIsoDate
import viewModel.TimerViewModel


@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddTimer(
    navController: NavController,
    onAddNewTimer: (CountdownTimer) -> Unit = {},

) {
    val viewModel: TimerViewModel = koinViewModel()
    val text = remember { mutableStateOf("") }

    val duration = remember { mutableStateOf(0L) }

    val colors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        focusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
        unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
        focusedContainerColor = Color.Gray.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    )

    ItemOverLayScaffold(
        title = "Neuen Timer hinzufügen",
        topNavigationButtons = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Localized description"
                )
            }
        },
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TextField(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Localized description"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                ,
                value = text.value,
                colors = colors,
                onValueChange = { text.value = it }
            )

            Text(
                text = "Dient zur bessern zu differenzierung.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            TextField(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Localized description"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                ,
                value = duration.value.toString(),
                colors = colors,
                onValueChange = { duration.value = it.toLong() },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(
                text = "Timerlaufzeit in Minuten",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate(Screens.Timer.route) }
            ) {
                Text("Abbrechen")
            }

            Button(
                enabled = true,
                onClick = {
                    viewModel.addTimer(name = text.value, duration = duration.value)
                    navController.navigate(Screens.Timer.route)
                }
            ) {
                Text("Speichern")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddTimerPreview() {
    AppTheme {
        AddTimer(navController = NavController(LocalContext.current))
    }
}