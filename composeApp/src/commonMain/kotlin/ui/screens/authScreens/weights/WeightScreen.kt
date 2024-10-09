package ui.screens.authScreens.weights

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ui.navigations.Destination
import viewModel.WeightScreenViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_gear
import data.repositories.FirebaseRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.androidx.compose.get
import ui.components.theme.button.IconButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import viewModel.FirebaseAuthViewModel
import viewModel.HomeViewModel

data class Difference(
    val delta: Double,
    val previousDate: String,
    val currentDate: String
)

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.weight(
    navController: NavHostController
) {
    composable(Destination.Weight.route) {
        val viewModel = viewModel<HomeViewModel>()
        val weeklyAverage by viewModel.weeklyAverage.collectAsStateWithLifecycle()

        val differences = weeklyAverage.zipWithNext { previous, current ->
            val delta = current.avgValue - previous.avgValue
            Difference(
                delta = delta,
                previousDate = previous.week,
                currentDate = current.week
            )
        }

        // Ermittlung der höchsten Differenz
        val maxDifference = differences.maxByOrNull { it.delta }

        // Ermittlung der tiefsten Differenz
        val minDifference = differences.minByOrNull { it.delta }

        val startWeight = remember {
            mutableDoubleStateOf(0.0)
        }

        val totalWeightLoss = remember {
            mutableDoubleStateOf(0.0)
        }
        weeklyAverage.lastOrNull()?.let {
            totalWeightLoss.doubleValue = startWeight.doubleValue - it.avgValue
        }

        LaunchedEffect(Unit) {
            Firebase.auth.currentUser?.let { userProfile ->
                FirebaseRepository().readUserProfileById(userProfile.uid)?.let {
                    startWeight.doubleValue = it.startWeight
                }
            }
        }


        ScreenHolder(
            title = Destination.Weight.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_add_timer,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.AddWeight.route)
                }

                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            },
        ) {

            Section {
                if(totalWeightLoss.doubleValue == 0.0) {
                    HeadlineText(text = "Dein Startgewicht ist ${"%.2f".format(startWeight.doubleValue)} kg")
                } else {
                    BodyText(text = "Totaler Gewicht Verlust: ${"%.2f".format(totalWeightLoss.doubleValue)} kg")
                }
            }

            Column {
                FooterText(modifier = Modifier.padding(start = 10.dp), text = "Größte Gewichtsabnahme")
                Section {
                    // Ausgabe der tiefsten Differenz
                    Column {
                        if (minDifference != null) {
                            BodyText("Differenz: ${"%.2f".format(minDifference.delta)} kg")
                            BodyText("Von: ${minDifference.previousDate} zu: ${minDifference.currentDate}")
                        } else {
                            BodyText("\nKeine Differenzen vorhanden.")
                        }
                    }
                }
            }

            Column {
                FooterText(modifier = Modifier.padding(start = 10.dp), text = "Größte Gewichtssteigerung:")
                Section {
                    Column {
                        if (maxDifference != null) {
                            BodyText("Differenz: +${"%.2f".format(maxDifference.delta)} kg")
                            BodyText("Von: ${maxDifference.previousDate} zu: ${maxDifference.currentDate}")
                        } else {
                            BodyText("\nKeine Differenzen vorhanden.")
                        }
                    }
                }
            }



            Column {
                FooterText(modifier = Modifier.padding(start = 10.dp), text = "VerlustHistory")
                Section {
                    Column {
                        differences.forEach {
                            val sign = if (it.delta >= 0) "+" else ""
                            FooterText("${it.previousDate} zu ${it.currentDate}: $sign${"%.2f".format(it.delta)} kg")
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun AllWeighsButton(text: String, navController: NavController) {
    Text(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(16.dp)
            .clickableWithRipple {
                navController.navigate(Destination.ShowAllWeights.route)
            }
        ,
        text = text
    )
}

@Composable
@Preview(showBackground = true)
fun AllWeighsButtonPreview() {
    val navController = NavController(LocalContext.current)
    AllWeighsButton(navController = navController, text = "Alle Gewichtungseinträge")
}
