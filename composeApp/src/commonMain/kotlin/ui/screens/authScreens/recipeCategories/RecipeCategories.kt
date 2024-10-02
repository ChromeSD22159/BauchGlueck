package ui.screens.authScreens.recipeCategories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.img_beilage
import bauchglueck.composeapp.generated.resources.img_dessert
import bauchglueck.composeapp.generated.resources.img_fueh_phase
import bauchglueck.composeapp.generated.resources.img_hauptgericht
import bauchglueck.composeapp.generated.resources.img_low_carb
import bauchglueck.composeapp.generated.resources.img_low_fat
import bauchglueck.composeapp.generated.resources.img_proteinreich
import bauchglueck.composeapp.generated.resources.img_puerierte_phase
import bauchglueck.composeapp.generated.resources.img_snack
import bauchglueck.composeapp.generated.resources.img_weiche_kost
import data.model.RecipeCategory
import org.jetbrains.compose.resources.painterResource
import ui.components.extentions.getSize
import ui.components.theme.ScreenHolder
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import ui.navigations.NavKeys
import ui.navigations.setNavKey
import viewModel.RecipeViewModel
import kotlin.math.ceil

fun NavGraphBuilder.recipeCategories(
    navController: NavHostController,
    recipeViewModel: RecipeViewModel
) {
    composable(Destination.RecipeCategories.route) { backStackEntry ->
        ScreenHolder(
            title = Destination.RecipeCategories.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            },
            optionsRow = {}
        ) {
            val recipes by recipeViewModel.foundRecipes.collectAsStateWithLifecycle()
            val categories = recipes
                .flatMap { it.categories }
                .map { it.name }
                .distinct() // Remove duplicates
                .map { name ->
                    name
                }



            val size = remember { mutableStateOf(IntSize.Zero) }

            val itemsPerRow = 2
            val gap = 16.dp
            val cardSizePx = (size.value.width / itemsPerRow)
            val cardRows = ceil(recipes.size / itemsPerRow.toDouble()).toInt()

            // Umrechnung von Pixel in dp
            val cardSizeDp = with(LocalDensity.current) { cardSizePx.toDp() }
            val gridSizeDp = (cardSizeDp + gap) * cardRows


            LazyVerticalGrid(
                modifier = Modifier
                    .height(gridSizeDp)
                    .getSize {
                        size.value = it
                    }
                    .fillMaxWidth(),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(categories.size) {
                    val category = categories[it]
                    val image = when (RecipeCategory.fromStrong(category)) {
                        RecipeCategory.HAUPTGERICHT -> Res.drawable.img_hauptgericht
                        RecipeCategory.BEILAGE -> Res.drawable.img_beilage
                        RecipeCategory.DESSERT -> Res.drawable.img_dessert
                        RecipeCategory.FRUEH_PHASE -> Res.drawable.img_fueh_phase
                        RecipeCategory.PUERIERTE_PHASE -> Res.drawable.img_puerierte_phase
                        RecipeCategory.WEICHE_KOST -> Res.drawable.img_weiche_kost
                        RecipeCategory.NORMALE_KOST -> Res.drawable.img_beilage // TODO FEHLT
                        RecipeCategory.PROTEINREICH -> Res.drawable.img_proteinreich
                        RecipeCategory.LOW_FAT -> Res.drawable.img_low_fat
                        RecipeCategory.LOW_CARB -> Res.drawable.img_low_carb
                        RecipeCategory.SNACK -> Res.drawable.img_snack
                        null -> Res.drawable.img_beilage
                    }

                   Card {
                       Box(
                           modifier = Modifier
                               .fillMaxWidth()
                               .clickableWithRipple {
                                   navController.navigate(Destination.RecipeList.route)
                                   navController.setNavKey(NavKeys.RecipeCategory, category.lowercase())
                                   navController.setNavKey(NavKeys.Destination, Destination.RecipeCategories.route)
                               },
                           contentAlignment = Alignment.BottomCenter
                       ) {
                           Image(
                               modifier = Modifier.fillMaxWidth(),
                               painter = painterResource(resource = image),
                               contentDescription = "Bild $category",
                               contentScale = ContentScale.FillWidth,
                           )
                           Row(
                               modifier = Modifier
                                   .background(MaterialTheme.colorScheme.surface.copy(0.95f))
                                   .fillMaxWidth()
                                   .padding(4.dp),
                               horizontalArrangement = Arrangement.Center
                           ) {
                               HeadlineText(text = category, size = 16.sp)
                           }
                       }
                   }
                }
            }
        }
    }
}