package ui.screens.authScreens.weights

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import ui.navigations.Destination
import viewModel.WeightScreenViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_add_timer
import bauchglueck.composeapp.generated.resources.ic_gear
import ui.components.theme.button.IconButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.ScreenHolder

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.weight(navController: NavHostController) {
    composable(Destination.Weight.route) {
        val viewmodel = viewModel<WeightScreenViewModel>()

        ScreenHolder(
            title = Destination.Weight.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_add_timer,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.AddWeight.route)
                }

                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            },
        ) {
            AllWeighsButton(
                navController = navController,
                text = "Alle Gewichtungseinträge"
            )
        }
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
