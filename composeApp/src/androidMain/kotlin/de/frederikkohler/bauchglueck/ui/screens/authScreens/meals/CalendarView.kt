package de.frederikkohler.bauchglueck.ui.screens.authScreens.meals

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import navigation.Screens

@Composable
fun CalendarView(navController: NavController) {
    BackScaffold(
        title = Screens.Calendar.title,
        navController = navController
    )
}