package de.frederikkohler.bauchglueck.ui.screens.authScreens.weight

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import navigation.Screens
import viewModel.WeightViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightScreen(
    navController: NavController,
    backNavigationDirection: Destination = Destination.Home
) {
    BackScaffold(
        title = Screens.Weight.title,
        backNavigationDirection = backNavigationDirection,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = R.drawable.ic_add_timer,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddWeight.route)
                    }
                )
            }

            Row {
                RoundImageButton(
                    icon = R.drawable.ic_gear,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        navController = navController
    ) {

    }
}