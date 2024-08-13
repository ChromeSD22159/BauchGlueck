package de.frederikkohler.bauchglueck.ui.screens.authScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import navigation.Screens

@Composable
fun WeightView(navController: NavController) {
    BackScaffold(
        title = Screens.Weight.title,
        navController = navController
    )
}