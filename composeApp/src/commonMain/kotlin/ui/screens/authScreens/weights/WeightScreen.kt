package ui.screens.authScreens.weights

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ui.components.BackScaffold
import ui.components.RoundImageButton
import ui.navigations.Destination
import viewModel.WeightScreenViewModel
import androidx.compose.ui.platform.LocalContext
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_gear
import ui.components.clickableWithRipple
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightScreen(
    navController: NavController,
    backNavigationDirection: Destination = Destination.Home
) {
    val viewmodel = koinViewModel<WeightScreenViewModel>()

    BackScaffold(
        title = Destination.Weight.title,
        backNavigationDirection = backNavigationDirection,
        topNavigationButtons = {
            Row {
                RoundImageButton(
                    icon = Res.drawable.ic_add_timer,
                    modifier = Modifier.padding(end = 16.dp),
                    action = {
                        navController.navigate(Destination.AddWeight.route)
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
        navController = navController,
    ) {
        AllWeighsButton(
            navController = navController,
            text = "Alle Gewichtungseinträge"
        )
    }
}

@Composable
fun AllWeighsButton(text: String, navController: NavController) {
    Text(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .fillMaxWidth()
            .padding(16.dp)
            .clickableWithRipple {
                navController.navigate(Destination.ShowAllWeights.route)
            }
        ,
        text = text
    )
}

@Composable
@Preview(showBackground = true)
fun AllWeighsButtonPreview() {
    val navController = NavController(LocalContext.current)
    AllWeighsButton(navController = navController, text = "Alle Gewichtungseinträge")
}
