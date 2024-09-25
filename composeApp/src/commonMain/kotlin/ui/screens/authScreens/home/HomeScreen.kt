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
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import data.local.entitiy.TimerState
import ui.navigations.Destination
import kotlinx.coroutines.launch
import ui.components.theme.IconCard
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.ScreenHolder
import viewModel.FirebaseAuthViewModel
import viewModel.HomeViewModel
import viewModel.TimerScreenViewModel
import viewModel.WeightScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.home(
    navController: NavHostController,
    showContentInDevelopment: Boolean,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    composable(Destination.Home.route) {

        val scope = rememberCoroutineScope()
        val viewModel = viewModel<HomeViewModel>()
        val medications by viewModel.medicationsWithIntakeDetailsForToday.collectAsStateWithLifecycle()
        val medicationListNotTakenToday by viewModel.medicationListNotTakenToday.collectAsStateWithLifecycle()
        val weeklyAverage by viewModel.weeklyAverage.collectAsStateWithLifecycle()
        val timers by viewModel.allTimers.collectAsStateWithLifecycle()

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

            if(showContentInDevelopment) {
                HomeCalendarCard {
                    scope.launch {
                        navController.navigate(Destination.MealPlanCalendar.route)
                    }
                }
            }

            HomeWeightCard(weeklyAverage) {
                scope.launch {
                    navController.navigate(Destination.Weight.route)
                }
            }

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
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        text = "Notiz eintragen"
                    ) {
                        navController.navigate(Destination.AddNote.route)
                    }
                }
            }

            LastNotesCalendar {
                navController.navigate(it.route)
            }

            WaterIntakeCard(firebaseAuthViewModel = firebaseAuthViewModel)

            NextMedicationCard(
                navController = navController,
                medications = medications,
                medicationListNotTakenToday = medicationListNotTakenToday
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}