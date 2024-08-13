package de.frederikkohler.bauchglueck.ui.screens.authScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import navigation.Screens

@Composable
fun WaterIntakeView(navController: NavController) {
    BackScaffold(
        title = Screens.WaterIntake.title,
        navController = navController
    )
}