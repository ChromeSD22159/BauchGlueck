package ui.screens.authScreens.shoppingListDetail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_arrow_up
import bauchglueck.composeapp.generated.resources.ic_gear
import data.local.entitiy.ShoppingList
import data.model.IngredientUnit
import data.remote.model.ShoppingListItem
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import ui.navigations.NavKeys
import util.toDateString
import viewModel.ShoppingListViewModel

fun NavGraphBuilder.shoppingListDetail(
    navController: NavHostController
) {
    composable(Destination.ShoppingListDetail.route) { backStackEntry ->

        val selectedCategory = backStackEntry.savedStateHandle.get<String>(NavKeys.RecipeId.key)
        val destination = backStackEntry.savedStateHandle.get<String>("destination")
        val viewModel: ShoppingListViewModel = viewModel<ShoppingListViewModel>()
        val currentShoppingList by viewModel.currentShoppingList.collectAsState()

        LaunchedEffect(Unit) {
            selectedCategory?.let {
                viewModel.loadShoppingListByID(it)
            }
        }

        ScreenHolder(
            title = Destination.ShoppingListDetail.title,
            showBackButton = true,
            onNavigate = {
                destination?.let {
                    navController.navigate(it)
                }
            },
            optionsRow = {
                IconButton(
                    resource = Res.drawable.ic_gear,
                    tint = MaterialTheme.colorScheme.onPrimary
                ) {
                    viewModel.clearCurrentShoppingList()
                    navController.navigate(Destination.Settings.route)
                }
            },
            pageSpacing = 0.dp,
            itemSpacing = 24.dp
        ){

            // ITEM LIST
            currentShoppingList?.let { list ->
                Header(list)

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    list.items.forEachIndexed { index, shoppingListItem ->
                        ShoppingListItem(shoppingListItem) {
                            viewModel.updateIsCompleteFromShoppingListItem(
                                shoppingListItemId = shoppingListItem.shoppingListItemId,
                                newState = shoppingListItem.isComplete
                            )
                        }
                    }
                }
            }
            
            // CONTROLS
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                currentShoppingList?.let {
                    TextButton(text = if(it.isComplete) "Erledigt" else "Nicht Erledigt") {
                        if(it.isComplete) viewModel.markListAsInComplete(it)
                        else viewModel.markListAsComplete(it)

                        viewModel.loadShoppingListByID(it.shoppingListId)
                    }

                    TextButton(text = "Löschen") {
                        viewModel.softDeleteShoppingList(it)
                        navController.navigate(Destination.ShoppingLists.route)
                    }
                }
            }
        }
    }
}

@Composable
fun Header(
    currentShoppingList: ShoppingList
) {
    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        HeadlineText("Einkaufsliste")

        FooterText("Alle Zutaten deiners Mealsplans im Zeitraum von ${currentShoppingList.startDate.displayDate} bis ${currentShoppingList.endDate.displayDate}.")
    }
}

@Composable
fun ShoppingListItem(
    shoppingListItem: ShoppingListItem,
    onUpdate: (ShoppingListItem) -> Unit = {}
) {
    val animatedColor by animateColorAsState(
        if (shoppingListItem.isComplete) MaterialTheme.colorScheme.onBackground.copy(0.5f)
        else MaterialTheme.colorScheme.onBackground.copy(1.0f),
        label = "color"
    )

    Section(
        sectionModifier = Modifier
            .padding(horizontal = 10.dp)
            .clickableWithRipple {
                shoppingListItem.isComplete = !shoppingListItem.isComplete
                onUpdate(shoppingListItem)
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyText(
                color = animatedColor,
                text = shoppingListItem.name
            )

            BodyText(
                color =  animatedColor,
                text = "${shoppingListItem.amount} ${IngredientUnit.fromUnitString(shoppingListItem.unit)?.name ?: shoppingListItem.unit}"
            )
        }
    }
}


val String.displayDate: String
    get() = LocalDate.parse(this).toDateString()