import model.recipe.Recipe

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun getRecipeById(id: Int): Recipe?
    suspend fun addRecipe(recipe: Recipe): Recipe?
    suspend fun editRecipe(recipe: Recipe): Recipe?
    suspend fun deleteRecipe(id: Int): Boolean
    suspend fun syncRecipes()
}