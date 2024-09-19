package ui.screens.authScreens.weights.addWeight

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_minus
import bauchglueck.composeapp.generated.resources.ic_plus
import data.local.entitiy.Weight
import ui.components.ItemOverLayScaffold
import ui.components.theme.clickableWithRipple
import ui.navigations.Destination
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import org.lighthousegames.logging.logging
import ui.components.FormScreens.FormControlButtons
import ui.components.theme.button.IconButton
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.navigations.NavigationTransition
import util.UUID
import viewModel.WeightScreenViewModel
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addWeight(navController: NavHostController) {
    composable(
        route = Destination.AddWeight.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddWeightScreen(
            navController = navController,
            onDismiss = {
                navController.navigate(Destination.Weight.route)
            }
        )
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AddWeightScreen(
    navController: NavController,
    steps: Double = 0.1,
    onDismiss: () -> Unit = {},
) {
    val viewModel = viewModel<WeightScreenViewModel>()
    val lastWeight by viewModel.lastWeight.collectAsState(initial = null)

    val currentWeight = remember {
        mutableStateOf(
            Weight(
                weightId = UUID.randomUUID(),
                userId = Firebase.auth.currentUser?.uid ?: "",
                value = 0.0,
                weighed = Clock.System.now().toString(),
                updatedAtOnDevice = Clock.System.now().toEpochMilliseconds(),
                createdAt = Clock.System.now().toString(),
                updatedAt = Clock.System.now().toString()
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.lastWeight.collect {
            if (it != null) {
                currentWeight.value.value = it.value
            }
        }
    }

    val weightDifference by remember {
        derivedStateOf {
            currentWeight.value.value - (lastWeight?.value ?: 0.0)
        }
    }

    val calculatedDifferenceProgressNew by remember {
        derivedStateOf {
            lastWeight?.let { lastWeight ->
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
            lastWeight?.let { lastWeight ->
                val lastWeighedDate = Instant.parse(lastWeight.weighed)
                val currentDate = Clock.System.now()

                val daysBetween = currentDate.periodUntil(lastWeighedDate, TimeZone.currentSystemDefault()).days
                abs(daysBetween)
            } ?: 0
        }
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

    val scrollStateWeightValues = rememberScrollState()
    val weightList = (50..200 step 10).map { it.toDouble() }

    ItemOverLayScaffold(
        title = "Neues Gewicht hinzufügen",
        topNavigationButtons = {},
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
                    BodyText(weightDifference.displayDifferenceWeight())

                    FooterText(
                        text = daysSinceLastWeighing.displayDaysSinceWeightString(),
                        textAlign = TextAlign.Center
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

            IconButton(
                resource = Res.drawable.ic_minus
            ) { decreaseWeight() }

            BodyText(currentWeight.value.value.displayCurrentWeight())

            IconButton(
                resource = Res.drawable.ic_plus
            ) { increaseWeight() }
        }


        // WEIGHT VALUES
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollStateWeightValues)
        ) {
            for (value in weightList) {
                BodyText(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickableWithRipple {
                            currentWeight.value = currentWeight.value.copy(value = value)
                        },
                    color = MaterialTheme.colorScheme.primary,
                    text = "${value.toInt()}kg"
                )
            }
        }


        // SAVE CONTROLS
        FormControlButtons(
            onCancel = {
                navController.navigate(Destination.Timer.route)
                onDismiss()
            },
            onSave = {
                viewModel.addItem(currentWeight.value)
                navController.navigate(Destination.Home.route)
            }
        )
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