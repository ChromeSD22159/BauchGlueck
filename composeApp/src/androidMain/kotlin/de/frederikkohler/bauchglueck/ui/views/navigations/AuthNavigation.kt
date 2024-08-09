package de.frederikkohler.bauchglueck.ui.views.navigations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.frederikkohler.bauchglueck.androidViewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.views.HomeView
import de.frederikkohler.bauchglueck.ui.views.LoginView
import de.frederikkohler.bauchglueck.ui.views.RegisterView
import model.LoginNav

@Composable
fun AuthNavigation(
    viewModel: FirebaseAuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navState by viewModel.nav.observeAsState()

    LaunchedEffect(navState) {
        navState?.let { navController.navigate(it.name) }
    }

    NavHost(navController = navController, startDestination = LoginNav.Login.name) {
        composable(LoginNav.Login.name) {
            LoginView( { viewModel.navigateTo(it) } )
        }
        composable(LoginNav.SignUp.name) {
            RegisterView( { viewModel.navigateTo(it) } )
        }
        composable(LoginNav.Logged.name) {
            HomeView()
        }
    }
}