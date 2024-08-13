package de.frederikkohler.bauchglueck.ui.navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarView
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeView
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PublicNavigation(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel
) {
    val navState by viewModel.user.collectAsStateWithLifecycle()
    navState?.let { navController.navigate(Screens.Home.name) }

    NavHost(navController = navController, startDestination = Screens.Launch.name) {
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
            HomeView(
                firebaseAuthViewModel = viewModel,
                navController = navController
            )
        }
        composable(Screens.Calendar.route) {
            CalendarView(
                navController = navController
            )
        }
        composable(Screens.Timer.route) {
            BackScaffold(
                title = Screens.Timer.title,
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

