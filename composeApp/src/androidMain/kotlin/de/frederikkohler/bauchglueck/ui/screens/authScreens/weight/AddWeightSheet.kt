package de.frederikkohler.bauchglueck.ui.screens.authScreens.weight

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import data.local.entitiy.Weight
import de.frederikkohler.bauchglueck.ui.components.ItemOverLayScaffold
import de.frederikkohler.bauchglueck.ui.components.clickableWithRipple
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging
import kotlin.math.abs

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddWeightSheet(
    navController: NavController,
    onDismiss: () -> Unit = {},
    lastWeight: Double,
    onSaved: (Weight) -> Unit = {}
) {
    val scrollStateWeightValues = rememberScrollState()
    val weightValue = remember { mutableDoubleStateOf(lastWeight) }
    val weightList = (50..200 step 10).map { it.toDouble() }
    val text = remember { mutableStateOf("%.1f".format(lastWeight) + "kg") }
    val steps = 0.5

    LaunchedEffect(weightValue.doubleValue) {
        text.value = "%.1f".format(weightValue.doubleValue) + "kg"
    }

    fun increaseWeight() {
        weightValue.doubleValue += steps
    }

    fun decreaseWeight() {
        weightValue.doubleValue -= steps
    }

    fun calculateWeightChangePercentage(): Float {
        if (lastWeight == 0.0) {
            return 0f // Handle division by zero
        }

        val difference = weightValue.doubleValue - lastWeight
        val percentageChange = (difference / lastWeight).coerceIn(-1.0, 1.0)

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
            Box() {

                CircularProgressIndicator(
                    progress = { calculateWeightChangePercentage() },
                    modifier = Modifier.size(150.dp),
                )

                Text(
                    text = "%.1f".format( weightValue.doubleValue - lastWeight ) + "kg",
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
                            weightValue.doubleValue = value
                        },
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    text = "${value.toInt()}kg"
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HorizontalSlidePreview() {
    val weightList = (50..200 step 10).map { it.toDouble() }

    AppTheme {
        Row {
            for (value in weightList) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickableWithRipple {},
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = MaterialTheme.typography.headlineLarge.fontStyle,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    text = "${value.toInt()}kg"
                )
            }
        }
    }
}