package de.frederikkohler.bauchglueck.ui.navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.koinViewModel
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.AddTimer
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.TimerScreen
import viewModel.TimerViewModel
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.delay
import navigation.Screens
import org.koin.compose.KoinContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel,
    onLoad: () -> Unit = {}
) {
    val user = Firebase.auth.currentUser

    KoinContext {

        LaunchedEffect(user) {
            onLoad()

            delay(700)

            if (user != null) {
                navController.navigate(Screens.Home.route)
            } else {
                navController.navigate(Screens.Login.route)
            }
        }

        NavHost(navController = navController, startDestination = Screens.Launch.route) {
            composable(Screens.Launch.route) {
                LaunchScreen()
            }
            composable(Screens.Login.route) {
                LoginView( { navController.navigate(it.route) } )
            }
            composable(Screens.SignUp.route) {
                RegisterView( { navController.navigate(it.route) } )
            }
            composable(Screens.Home.route) {
                HomeScreen(
                    firebaseAuthViewModel = viewModel,
                    navController = navController
                )
            }
            composable(Screens.Calendar.route) {
                CalendarScreen(
                    navController = navController
                )
            }
            composable(Screens.Timer.route) {
                val vm = koinViewModel<TimerViewModel>()
                TimerScreen(
                    navController = navController
                )
            }
            composable(Screens.Weight.route) {
                BackScaffold(
                    title = Screens.Weight.title,
                    navController = navController
                )
            }
            composable(Screens.WaterIntake.route) {
                BackScaffold(
                    title = Screens.WaterIntake.title,
                    navController = navController
                )
            }
            composable(
                route = Screens.AddTimer.route,
                enterTransition = { slideInWithFadeToTopAnimation(this) },
                exitTransition = { slideOutWithFadeToTopAnimation(this) }
            ) {
                AddTimer(navController)
            }

        }
    }
}

fun slideInWithFadeToTopAnimation(scope: AnimatedContentTransitionScope<NavBackStackEntry>): EnterTransition {
    return slideInVertically(
        initialOffsetY = { -it },
        animationSpec = tween(250)
    ) + scaleIn(
        animationSpec = tween(250)
    )
}

fun slideOutWithFadeToTopAnimation(scope: AnimatedContentTransitionScope<NavBackStackEntry>): ExitTransition {
    return slideOutVertically(
        animationSpec = tween(250)
    ) + scaleOut(
        animationSpec = tween(250)
    )
}



