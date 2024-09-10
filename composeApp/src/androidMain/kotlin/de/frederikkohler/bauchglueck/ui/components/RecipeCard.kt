package de.frederikkohler.bauchglueck.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import data.local.entitiy.MealWithCategories
import de.frederikkohler.bauchglueck.R
import di.serverHost

@Composable
fun RecipeCard(
    mealWithCategories: MealWithCategories
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Image Section
            Row(
                modifier = Modifier
                    .weight(1.75f)
                    .fillMaxHeight() 
            ) {

                AsyncImage(
                    model = serverHost + mealWithCategories.meal.mainImage?.formats?.small?.url,
                    placeholder = painterResource(R.drawable.placeholder_image),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.fillMaxHeight()
                )

            }

            // Right Text Section
            Row(
                modifier = Modifier
                    .weight(3.25f)
                    .padding(8.dp)
                    .fillMaxHeight() 
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextElement(
                        text = mealWithCategories.meal.name,
                        component = TextComponent.HeadLine,
                        fontSize = 14.sp
                    )

                    TextElement(
                        text = mealWithCategories.meal.description,
                        component = TextComponent.ContinuousText,
                        maxLines = 4,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconListWithText(
                            rowModifier = Modifier.weight(1f),
                            items = listOf(
                                Pair(R.drawable.ic_protein, "${mealWithCategories.meal.protein}g"),
                                Pair(R.drawable.ic_fat, "${mealWithCategories.meal.fat}g")
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconListWithText(
                            rowModifier = Modifier.weight(1f),
                            items = listOf(
                                Pair(R.drawable.ic_sugar, "${mealWithCategories.meal.sugar}g"),
                                Pair(R.drawable.ic_kcal, "${mealWithCategories.meal.kcal.decimal(0)}g")
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconListWithText(
                            rowModifier = Modifier.weight(1f),
                            items = listOf(
                                Pair(R.drawable.ic_grid_2_2, mealWithCategories.categories.firstOrNull { true }?.name ?: "No Category"),
                                Pair(R.drawable.ic_stopwatch, "${mealWithCategories.meal.preparationTimeInMinutes} min.")
                            )
                        )
                    }
                }
            }
        }
    }
}

fun Double.decimal(int: Int = 1): String {
    return "%.${int}f".format(this)
}