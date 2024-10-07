package ui.screens.authScreens.shoppingListDetail

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import ui.components.theme.ScreenHolder
import ui.components.theme.button.IconButton
import ui.navigations.Destination

fun NavGraphBuilder.shoppingListDetail(
    navController: NavHostController
) {
    composable(Destination.ShoppingListDetail.route) { backStackEntry ->
        ScreenHolder(
            title = Destination.ShoppingListDetail.title,
            showBackButton = false,
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Settings.route)
                }
            },
            pageSpacing = 0.dp,
            itemSpacing = 24.dp
        ){

        }
    }
}