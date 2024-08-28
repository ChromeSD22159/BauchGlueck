package de.frederikkohler.bauchglueck.ui.screens.authScreens.meals

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    backNavigationDirection: Destination = Destination.Home
) {
    BackScaffold(
        title = Screens.Calendar.title,
        backNavigationDirection = backNavigationDirection,
        navController = navController
    )
}