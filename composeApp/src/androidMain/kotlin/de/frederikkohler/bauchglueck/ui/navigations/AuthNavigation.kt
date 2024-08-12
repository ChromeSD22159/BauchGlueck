package de.frederikkohler.bauchglueck.ui.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.authScreens.HomeView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import navigation.PublicNav

@Composable
fun AuthNavigation(
    viewModel: FirebaseAuthViewModel
) {
    val navController = rememberNavController()
    val navState by viewModel.nav.collectAsStateWithLifecycle()

    LaunchedEffect(navState) {
        navState.let { navController.navigate(it.name) }
    }

    NavHost(navController = navController, startDestination = PublicNav.Login.name) {
        composable(PublicNav.Login.name) {
            LoginView( { viewModel.navigateTo(it) } )
        }
        composable(PublicNav.SignUp.name) {
            RegisterView( { viewModel.navigateTo(it) } )
        }
        composable(PublicNav.Logged.name) {
            HomeView()
        }
    }
}