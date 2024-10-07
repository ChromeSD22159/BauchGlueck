package ui.screens.authScreens.shoppingList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_ellipsis
import bauchglueck.composeapp.generated.resources.ic_gear
import bauchglueck.composeapp.generated.resources.ic_plus
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.vectorResource
import org.lighthousegames.logging.logging
import ui.components.DateRangePickerOverLay
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.button.IconButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.navigations.Destination
import util.DateRepository
import util.toDateString
import viewModel.ShoppingListViewModel

fun NavGraphBuilder.shoppingLists(
    navController: NavHostController
) {
    composable(Destination.ShoppingLists.route) { backStackEntry ->
        val viewModel: ShoppingListViewModel = viewModel<ShoppingListViewModel>()
        val showDatePicker by viewModel.showDatePicker.collectAsState()
        val shoppingLists by viewModel.shoppingLists.collectAsState()

        ScreenHolder(
            title = Destination.ShoppingLists.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    navController.navigate(Destination.Home.route)
                }
            },
            pageSpacing = 0.dp,
            itemSpacing = 12.dp
        ){

            if(shoppingLists.isEmpty()) {
                NoShoppingList {
                    viewModel.toggleDatePicker()
                }
            } else {
                shoppingLists.forEach { _ ->
                    ShoppingListItem(startDate = DateRepository.today, endDate = DateRepository.today, false)
                }
            }
        }



        DateRangePickerOverLay(
            showDatePicker,
            onDatePickerStateChange = { viewModel.toggleDatePicker() },
            onConformDate = { start, end->
                if(start != null && end != null) {
                    viewModel.startGenerateShoppingList(start, end)
                }
            }
        )
    }
}

@Composable
fun NoShoppingList(
    onClick: () -> Unit = {}
) {
    Section(
        sectionModifier = Modifier
            .clickableWithRipple { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                modifier = Modifier
                    .background(Color.White.copy(0.2f), shape = CircleShape)
                    .padding(2.dp),
                imageVector = vectorResource(resource = Res.drawable.ic_plus),
                contentDescription = ""
            )

            BodyText(
                modifier = Modifier,
                textAlign = TextAlign.Center,
                text = "Erstelle deine erste Einkaufsliste\nbasierend auf deinen Mealplan!"
            )
        }
    }
}

@Composable
fun ShoppingListItem(
    startDate: LocalDate,
    endDate: LocalDate,
    isDone: Boolean
) {
    Section(sectionModifier = Modifier.padding(horizontal = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            BodyText(
                modifier = Modifier,
                text = "${startDate.toDateString()} - ${endDate.toDateString()}"
            )

            Icon(
                modifier = Modifier
                    .background(Color.White.copy(0.2f), shape = CircleShape)
                    .padding(2.dp),
                imageVector = vectorResource(resource = Res.drawable.ic_ellipsis),
                contentDescription = ""
            )
        }
    }
}


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


fun NavGraphBuilder.shoppingListGenerate(
    navController: NavHostController
) {
    composable(Destination.ShoppingListGenerate.route) { backStackEntry ->
        ScreenHolder(
            title = Destination.ShoppingListGenerate.title,
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