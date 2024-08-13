package de.frederikkohler.bauchglueck.ui.screens.authScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.ui.components.ChildView
import navigation.Screens

@Composable
fun CalendarView(navController: NavController) {
    ChildView(
        title = Screens.Calendar.title,
        navController = navController
    )
}