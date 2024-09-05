package de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.addWeight

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import data.local.entitiy.Weight
import de.frederikkohler.bauchglueck.ui.components.ItemOverLayScaffold
import de.frederikkohler.bauchglueck.ui.components.clickableWithRipple
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import org.koin.androidx.compose.koinViewModel
import util.generateDeviceId
import viewModel.WeightScreenViewModel
import kotlin.math.abs

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddWeightScreen(
    navController: NavController,
    steps: Double = 0.1,
    onDismiss: () -> Unit = {},
) {
    val viewModel = koinViewModel<WeightScreenViewModel>()

    // Lade das letzte Gewicht, wenn der Composable geladen wird
    LaunchedEffect(Unit) {
        viewModel.getLastWeight()
    }

    // Beobachte den Zustand von lastWeight (aka latestWeight)
    val latestWeight by viewModel.lastWeight.collectAsState()

    // Merke den aktuellen Gewichtszustand
    val currentWeight: MutableState<Weight> = remember {
        mutableStateOf(
            Weight(
                weightId = generateDeviceId(),
                userId = Firebase.auth.currentUser?.uid ?: "",
                value = 0.0,
                weighed = Clock.System.now().toString()
            )
        )
    }

    // Wenn das latestWeight geladen wird, aktualisiere das currentWeight, falls es noch nicht gesetzt wurde
    LaunchedEffect(latestWeight) {
        latestWeight?.let { weight ->
            if (currentWeight.value.value == 0.0) {
                currentWeight.value = weight.copy(value = weight.value + 0.0)
            }
        }
    }

    // Berechnung der Gewichtsdifferenz und des Fortschritts
    val weightDifference by remember {
        derivedStateOf {
            latestWeight?.let { lastWeight ->
                currentWeight.value.value - lastWeight.value
            } ?: 0.0
        }
    }

    val calculatedDifferenceProgressNew by remember {
        derivedStateOf {
            latestWeight?.let { lastWeight ->
                if (lastWeight.value != 0.0) {
                    val percentageChange = (currentWeight.value.value - lastWeight.value) / lastWeight.value
                    abs(percentageChange).toFloat().coerceIn(0f, 1f)
                } else {
                    0f
                }
            } ?: 0f
        }
    }

    val daysSinceLastWeighing by remember {
        derivedStateOf {
            latestWeight?.let { lastWeight ->
                val lastWeighedDate = Instant.parse(lastWeight.weighed)
                val currentDate = Clock.System.now()

                val daysBetween = currentDate.periodUntil(lastWeighedDate, TimeZone.currentSystemDefault()).days
                abs(daysBetween)
            } ?: 0
        }
    }

    // Funktion zum Erhöhen des aktuellen Gewichts
    fun increaseWeight() {
        currentWeight.value = currentWeight.value.copy(
            value = currentWeight.value.value + steps
        )
    }

    // Funktion zum Verringern des aktuellen Gewichts
    fun decreaseWeight() {
        currentWeight.value = currentWeight.value.copy(
            value = currentWeight.value.value - steps
        )
    }

    // UI-Elemente und Logik für das Scrollen durch die Gewichtswerte etc.
    val scrollStateWeightValues = rememberScrollState()
    val weightList = (50..200 step 10).map { it.toDouble() }

    ItemOverLayScaffold(
        title = "Neues Gewicht hinzufügen",
        topNavigationButtons = {
            IconButton(onClick = {
                navController.navigate(Destination.Timer.route)
                onDismiss()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Localized description"
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { calculatedDifferenceProgressNew },
                    modifier = Modifier.size(150.dp),
                )

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = weightDifference.displayDifferenceWeight(),
                        fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                    )

                    Text(
                        text = daysSinceLastWeighing.displayDaysSinceWeightString(),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp
                    )
                }
            }
        }


        // WEIGHT CONTROLS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { decreaseWeight() }) {
                Text(text = "-")
            }

            Text(
                text = currentWeight.value.value.displayCurrentWeight(),
                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight
            )

            Button(onClick = { increaseWeight() }) {
                Text(text = "+")
            }
        }


        // WEIGHT VALUES
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollStateWeightValues)
        ) {
            for (value in weightList) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickableWithRipple {
                            currentWeight.value = currentWeight.value.copy(value = value)
                        },
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    text = "${value.toInt()}kg"
                )
            }
        }


        // SAVE CONTROLS
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        navController.navigate(Destination.Timer.route)
                        onDismiss()
                    }
                ) {
                    Text("Abbrechen")
                }

                Button(
                    onClick = {
                        viewModel.addItem(currentWeight.value)
                        navController.navigate(Destination.Home.route)
                    }
                ) {
                    Text("Speichern")
                }
            }
        }
    }
}

fun Double.displayCurrentWeight(): String {
    return "%.1f kg".format(this)
}

fun Double.displayDifferenceWeight(): String {
    val value = abs(this)
    return when {
        (this > 0) -> "+%.1f kg".format(value)
        (this < 0) -> "-%.1f kg".format(value)
        else -> "%.1f kg".format(value)
    }
}

fun Int.displayDaysSinceWeightString (): String {
    return when (this) {
        0 -> "Seit dem letzten\nWiegen heute"
        1 -> "Seit dem letzten\nWiegen gestern"
        else -> "Seit dem letzten\nWiegen vor\n$this Tagen"
    }
}