package de.frederikkohler.bauchglueck.ui.navigations

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.TimerScreen
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PublicNavigation(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel
) {
    val navState by viewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(navState) {
        if (navState != null) {
            navController.navigate(Screens.Home.route)
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

    }
}

