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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.AddEditTimerSheet
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.TimerScreen
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel
) {
    logging().info { "NavGraph" }

    val user = Firebase.auth.currentUser
    val timerViewmodel: TimerViewModel = koinViewModel()
    val scope = rememberCoroutineScope()

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
                LaunchedEffect(Unit) {
                    timerViewmodel.syncDataWithRemote()
                }
                LaunchScreen()
            }
            composable(Destination.Login.route) {
                LoginView( { navController.navigate(it.route) } )
            }
            composable(Destination.SignUp.route) {
                RegisterView( { navController.navigate(it.route) } )
            }
            composable(Destination.Home.route) {
                HomeScreen(
                    timerViewModel = timerViewmodel,
                    firebaseAuthViewModel = viewModel,
                    navController = navController
                )
            }
            composable(Destination.Calendar.route) {
                CalendarScreen(
                    navController = navController
                )
            }
            composable(Destination.Timer.route) {
                LaunchedEffect(Unit) {
                    timerViewmodel.getAllCountdownTimers()
                }
                TimerScreen(
                    navController = navController,
                    timerViewmodel,
                    onEdit = {
                        timerViewmodel.setSelectedTimer(it)
                        navController.navigate(Destination.EditTimer.route)
                    }
                )
            }
            composable(Destination.Weight.route) {
                BackScaffold(
                    title = Destination.Weight.title,
                    navController = navController
                )
            }
            composable(Destination.WaterIntake.route) {
                BackScaffold(
                    title = Destination.WaterIntake.title,
                    navController = navController
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
                    onSaved = {
                        scope.launch {
                            logging().info { "onSaved: $it" }
                            timerViewmodel.addTimer(it.name, it.duration)
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
                AddEditTimerSheet(
                    navController = navController,
                    currentCountdownTimer = timerViewmodel.uiState.value.selectedTimer,
                    onSaved = {
                        scope.launch {
                            logging().info { "onEdit: $it" }
                            timerViewmodel.updateTimerAndSyncRemote(it)
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



