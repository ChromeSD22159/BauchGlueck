package de.frederikkohler.bauchglueck.ui.navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.ui.components.ChildView
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.HomeView
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
        composable(Screens.Launch.name) {
            LaunchScreen()
        }
        composable(Screens.Login.name) {
            LoginView( { navController.navigate(it.name) } )
        }
        composable(Screens.SignUp.name) {
            RegisterView( { navController.navigate(it.name) } )
        }
        composable(Screens.Home.name) {
            HomeView(viewModel, navController)
        }
        composable(Screens.Settings.name) {
            ChildView(
                title = Screens.Settings.name,
                navController = navController
            )
        }
        composable(Screens.Test.name) {
            ChildView(
                title = Screens.Test.name,
                navController = navController
            )
        }
    }
}

