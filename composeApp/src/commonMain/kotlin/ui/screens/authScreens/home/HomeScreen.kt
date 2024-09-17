package ui.screens.authScreens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.icon_plus
import data.local.entitiy.TimerState
import ui.navigations.Destination
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.IconCard
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import viewModel.FirebaseAuthViewModel
import viewModel.TimerScreenViewModel
import viewModel.WaterIntakeViewModel
import viewModel.WeightScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.home(
    navController: NavHostController,
    showContentInDevelopment: Boolean,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    composable(Destination.Home.route) {
        val timerScreenViewModel = viewModel<TimerScreenViewModel>()
        val weightScreenViewModel = viewModel<WeightScreenViewModel>()

        val scope = rememberCoroutineScope()

        val dailyAverage by weightScreenViewModel.dailyAverage.collectAsState(initial = emptyList())
        val timers by timerScreenViewModel.allTimers.collectAsStateWithLifecycle(initialValue = emptyList())

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
            pageSpacing = 0.dp
        ) {

            if(showContentInDevelopment) {
                HomeCalendarCard {
                    scope.launch {
                        navController.navigate(Destination.MealPlanCalendar.route)
                    }
                }
            }


            HomeWeightCard(
                dailyAverage,
            ) {
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
                    )
                }
            }

            WaterIntakeCard(firebaseAuthViewModel = firebaseAuthViewModel)

            HomeWaterIntakeCard {
                scope.launch {
                    navController.navigate(Destination.WaterIntake.route)
                }
            }

            HomeMedicationCard {
                scope.launch {
                    navController.navigate(Destination.Medication.route)
                }
            }
        }
    }
}

