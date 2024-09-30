package ui.screens.authScreens.mealPlan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import org.lighthousegames.logging.logging
import ui.components.RecipeCard
import ui.navigations.Destination
import viewModel.RecipeViewModel

fun NavGraphBuilder.recipesComposable(navController: NavHostController) {
    composable(Destination.Recipes.route) {
        val recipeViewModel = viewModel<RecipeViewModel>()

        val localRecipe by recipeViewModel.recipes.collectAsStateWithLifecycle(initialValue = emptyList())

        LaunchedEffect(localRecipe) {
            logging().info { "localMealsState: ${localRecipe.size}" }
        }

        Column(
            modifier = Modifier.padding(top = 55.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "Rezepte"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(localRecipe.size) { meal ->
                    RecipeCard(localRecipe[meal])
                }
            }
        }
    }
}