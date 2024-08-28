package de.frederikkohler.bauchglueck.ui.screens.authScreens.waterIntake

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WaterIntakeScreen(
    navController: NavController,
    backNavigationDirection: Destination = Destination.Home
) {
    BackScaffold(
        title = Screens.WaterIntake.title,
        backNavigationDirection = backNavigationDirection,
        navController = navController
    )
}