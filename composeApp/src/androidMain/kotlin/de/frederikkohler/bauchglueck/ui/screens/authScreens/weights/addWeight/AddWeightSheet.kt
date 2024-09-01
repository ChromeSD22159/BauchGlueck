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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import data.local.entitiy.Weight
import de.frederikkohler.bauchglueck.ui.components.ItemOverLayScaffold
import de.frederikkohler.bauchglueck.ui.components.clickableWithRipple
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock
import util.generateDeviceId
import kotlin.math.abs

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddWeightSheet(
    navController: NavController,
    onDismiss: () -> Unit = {},
    lastWeight: Weight?,
    onSaved: (Weight) -> Unit = {}
) {

    val currentWeight: MutableState<Weight> = remember {
        mutableStateOf(Weight(
                weightId = generateDeviceId(),
                userId = Firebase.auth.currentUser!!.uid,
                weighed = Clock.System.now().toString(),
                value = 0.0
            )
        )
    }

    val scrollStateWeightValues = rememberScrollState()
    val weightList = (50..200 step 10).map { it.toDouble() }
    val text = remember { mutableStateOf("%.1f".format(currentWeight.value.value) + "kg") }
    val steps = 0.5

    LaunchedEffect(Unit) {
        lastWeight?.let {
            currentWeight.value = currentWeight.value.copy(value = it.value)
        }
    }

    // Aktualisiert den Text basierend auf dem aktuellen Gewichtswert
    LaunchedEffect(currentWeight.value) {
        text.value = "%.1f".format(currentWeight.value.value) + "kg"
    }

    fun increaseWeight() {
        currentWeight.value = currentWeight.value.copy(
            value = currentWeight.value.value + steps
        )
    }

    fun decreaseWeight() {
        currentWeight.value = currentWeight.value.copy(
            value = currentWeight.value.value - steps
        )
    }

    fun calculateWeightChangePercentage(): Float {
        if (lastWeight?.value == 0.0 || lastWeight?.value == null) {
            return 0f // Handle division by zero
        }

        val difference = currentWeight.value.value - lastWeight.value
        val percentageChange = (difference / lastWeight.value).coerceIn(-1.0, 1.0)

        return abs(percentageChange).toFloat()
    }

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
            Box {
                CircularProgressIndicator(
                    progress = { calculateWeightChangePercentage() },
                    modifier = Modifier.size(150.dp),
                )

                Text(
                    text = "%.1f".format(currentWeight.value.value - (lastWeight?.value ?: 0.0)) + "kg",
                    fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { decreaseWeight() }) {
                Text(text = "-")
            }

            Text(
                text = text.value,
                fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight
            )

            Button(onClick = { increaseWeight() }) {
                Text(text = "+")
            }
        }

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
                        onSaved(currentWeight.value)
                        navController.navigate(Destination.Timer.route)
                    }
                ) {
                    Text("Speichern")
                }
            }
        }
    }
}