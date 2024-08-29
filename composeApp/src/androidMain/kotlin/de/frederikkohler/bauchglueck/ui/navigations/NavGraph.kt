package de.frederikkohler.bauchglueck.ui.navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.AddEditTimerSheet
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.TimerScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weight.AddWeightSheet
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weight.WeightScreen
import viewModel.TimerViewModel
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.lighthousegames.logging.logging
import org.koin.androidx.compose.koinViewModel
import viewModel.WeightViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel
) {
    logging().info { "NavGraph" }

    val user = Firebase.auth.currentUser
    val scope = rememberCoroutineScope()
    val timerViewModel = koinViewModel<TimerViewModel>()

    KoinContext {
        LaunchedEffect(user) {
            delay(700)

            if (user != null) {
                navController.navigate(Destination.Home.route)
            } else {
                navController.navigate(Destination.Login.route)
            }
        }

        NavHost(navController = navController, startDestination = Destination.Launch.route) {
            composable(Destination.Launch.route) {
                LaunchScreen()
            }
            composable(Destination.Login.route) {
                LoginView( { navController.navigate(it.route) } )
            }
            composable(Destination.SignUp.route) {
                RegisterView( { navController.navigate(it.route) } )
            }
            composable(Destination.Home.route) {
                val weightViewModel = koinViewModel<WeightViewModel>()
                weightViewModel.getCardWeights()
                val dailyAverage by weightViewModel.uiState.value.dailyAverage.collectAsState()
                val timer by timerViewModel.uiState.value.timers.collectAsState()
                HomeScreen(
                    timers = timer,
                    dailyAverage = dailyAverage,
                    firebaseAuthViewModel = viewModel,
                    navController = navController
                )
            }
            composable(Destination.Calendar.route) {
                CalendarScreen(
                    navController = navController
                )
            }

            composable(Destination.Weight.route) {
                WeightScreen(
                    navController = navController,
                    backNavigationDirection = Destination.Home
                )
            }

            composable(Destination.AddWeight.route) {
                val weightViewModel = koinViewModel<WeightViewModel>()
                weightViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    weightViewModel.getLastWeight()
                }

                val lastWeight by weightViewModel.lastWeight.collectAsState()

                AddWeightSheet(
                    navController = navController,
                    lastWeight = lastWeight?.value ?: 0.0,
                    onDismiss = {
                        navController.navigate(Destination.Weight.route)
                    },
                    onSaved = {
                        scope.launch {
                            logging().info { "onSaved: $it" }
                            weightViewModel.addWeight(it.value)
                            navController.navigate(Destination.Weight.route)
                        }
                    }
                )

            }

            composable(Destination.WaterIntake.route) {
                BackScaffold(
                    title = Destination.WaterIntake.title,
                    navController = navController
                )
            }

            composable(Destination.Timer.route) {
                TimerScreen(
                    navController = navController,
                    timerViewModel,
                    onEdit = {
                        timerViewModel.setSelectedTimer(it)
                        navController.navigate(Destination.EditTimer.route)
                    }
                )
            }
            composable(
                route = Destination.AddTimer.route,
                enterTransition = { slideInWithFadeToTopAnimation() },
                exitTransition = { slideOutWithFadeToTopAnimation() }
            ) {
                AddEditTimerSheet(
                    navController = navController,
                    currentCountdownTimer = null,
                    onSaved = { timer ->
                        scope.launch {
                            logging().info { "onSaved: $timer" }
                            timerViewModel.addTimer(timer.name, timer.duration)
                            navController.navigate(Destination.Timer.route)
                        }
                    }
                )
            }

            composable(
                route = Destination.EditTimer.route,
                enterTransition = { slideInWithFadeToTopAnimation() },
                exitTransition = { slideOutWithFadeToTopAnimation() }
            ) {
                val selectedTimer by timerViewModel.uiState.value.selectedTimer.collectAsState()
                AddEditTimerSheet(
                    navController = navController,
                    currentCountdownTimer = selectedTimer,
                    onSaved = { timer ->
                        scope.launch {
                            logging().info { "onEdit: $timer" }
                            timerViewModel.updateTimerAndSyncRemote(timer)
                            navController.navigate(Destination.Timer.route)
                        }
                    }
                )
            }
        }
    }
}

sealed class Destination(val route: String, val title: String) {
    data object Launch : Destination("Launch", "Launch")
    data object Login : Destination("Login", "Login")
    data object SignUp : Destination("Register", "Register")
    data object Home : Destination("Home", "Home")
    data object Calendar : Destination("Calendar", "Kalender")
    data object Timer : Destination("Timer", "Timer")
    data object Weight : Destination("Weight", "Gewicht")
    data object AddWeight : Destination("AddWeight", "Gewicht hinzufügen")
    data object WaterIntake : Destination("WaterIntake", "Wasseraufnahme")
    data object AddTimer : Destination("AddTimer", "Timer hinzufügen")
    data object EditTimer : Destination("EditTimer", "Timer Bearbeiten")
}

fun slideInWithFadeToTopAnimation(): EnterTransition {
    return slideInVertically(
        initialOffsetY = { it.takeIf { it != Int.MIN_VALUE } ?: 0 },
        animationSpec = tween(250)
    ) + scaleIn(
        animationSpec = tween(250)
    ) + fadeIn(animationSpec = tween(250))
}

fun slideOutWithFadeToTopAnimation(): ExitTransition {
    return slideOutVertically(
        targetOffsetY = { it.takeIf { it != Int.MIN_VALUE } ?: 0 },
        animationSpec = tween(250)
    ) + scaleOut(
        animationSpec = tween(250)
    ) + fadeOut(animationSpec = tween(250))
}