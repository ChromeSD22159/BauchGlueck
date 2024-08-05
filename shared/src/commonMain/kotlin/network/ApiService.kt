package network

import model.recipe.MeasurementUnit
import model.recipe.Recipe
import model.recipe.RecipeCategory

interface ApiService {
    suspend fun fetchRecipeCategories(): List<RecipeCategory>
    suspend fun fetchMeasurementUnits(): List<MeasurementUnit>
    suspend fun fetchRecipes(): List<Recipe>
    suspend fun addRecipe(recipe: Recipe): Recipe?
    suspend fun updateRecipe(recipe: Recipe): Recipe?
    suspend fun deleteRecipe(id: Int): Boolean
}

