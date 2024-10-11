package ui.screens.authScreens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_cart_mirrored
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_kochhut
import bauchglueck.composeapp.generated.resources.ic_meal_plan
import data.local.entitiy.TimerState
import ui.navigations.Destination
import kotlinx.coroutines.launch
import ui.components.theme.IconCard
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.ScreenHolder
import viewModel.FirebaseAuthViewModel
import viewModel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.home(
    navController: NavHostController,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    composable(Destination.Home.route) {backStackEntry ->
        val scope = rememberCoroutineScope()
        val viewModel = viewModel<HomeViewModel>()
        val medications by viewModel.medicationsWithIntakeDetailsForToday.collectAsStateWithLifecycle()
        val weeklyAverage by viewModel.weeklyAverage.collectAsStateWithLifecycle()
        val timers by viewModel.allTimers.collectAsStateWithLifecycle()
        val nextMedication by viewModel.nextMedicationIntake.collectAsStateWithLifecycle()

        ScreenHolder(
            title = Destination.Home.title,
            showBackButton = false,
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            },
            pageSpacing = 0.dp,
            itemSpacing = 24.dp
        ) {

            SectionImageCard(
                image = Res.drawable.ic_meal_plan,
                title = "MealPlaner",
                description = "Erstelle deinen MealPlan, indifiduell auf deine bedürfnisse.",
                offset = DpOffset(0.dp, 0.dp),
                scale = 1.3f,
                onNavigate = {
                    navController.navigate(Destination.MealPlanCalendar.route)
                }
            )

            SectionImageCard(
                image = Res.drawable.ic_kochhut,
                title = "Rezepte",
                offset = DpOffset(0.dp, 0.dp),
                scale = 1.9f,
                description = "Stöbere durch rezepte und füge sie zu deinem Meal plan hinzu.",
                onNavigate = {
                    navController.navigate(Destination.RecipeCategories.route)
                }
            )

            SectionImageCard(
                image = Res.drawable.ic_cart_mirrored,
                title = "Shoppinglist",
                offset = DpOffset(0.dp, 0.dp),
                description = "Erstelle aus deinem Mealplan eine Shoppingliste.",
                scale = 1.4f,
                onNavigate = {
                    navController.navigate(Destination.ShoppingLists.route)
                }
            )

            WeightCardChartCard(
                hasValidData = weeklyAverage.any { it.avgValue > 0.0 },
                navController = navController,
                weeklyAverage = weeklyAverage
            )

            HomeTimerWidget(
                timers.sortedBy { TimerState.fromValue(it.timerState).state  }
            ) {
                scope.launch {
                    navController.navigate(it.route)
                }
            }

            IconCard(
                height = 150.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    TextButton(
                        text = "Notiz eintragen"
                    ) {
                        navController.navigate(Destination.AddNote.route)
                    }

                    TextButton(
                        text = "Alle Notizen"
                    ) {
                        navController.navigate(Destination.ShowAllNotes.route)
                    }
                }
            }

            WaterIntakeCard(firebaseAuthViewModel = firebaseAuthViewModel)

            NextMedicationCard(
                navController = navController,
                medications = medications,
                nextMedication = nextMedication
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}