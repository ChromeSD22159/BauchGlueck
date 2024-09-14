package ui.screens.authScreens.waterIntake

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_gear
import ui.components.BackScaffold
import ui.components.RoundImageButton
import ui.navigations.Destination

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WaterIntakeScreen(
    navController: NavController,
    backNavigationDirection: Destination = Destination.Home
) {
    BackScaffold(
        title = Destination.WaterIntake.title,
        navController = navController,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = Res.drawable.ic_add_timer,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddTimer.route)
                    }
                )
            }

            Row {
                RoundImageButton(
                    icon = Res.drawable.ic_gear,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        },
        backNavigationDirection = backNavigationDirection,
    ) {

    }
}