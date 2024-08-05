
import database.IngredientDao
import database.IngredientEntity
import database.MeasurementUnitDao
import database.MeasurementUnitEntity
import database.RecipeCategoryDao
import database.RecipeCategoryEntity
import database.RecipeDao
import database.RecipeEntity
import model.recipe.Recipe
import network.ApiService

class RecipeRepositoryImpl(
    private val apiService: ApiService,
    private val recipeDao: RecipeDao,
    private val recipeCategoryDao: RecipeCategoryDao,
    private val measurementUnitDao: MeasurementUnitDao,
    private val ingredientDao: IngredientDao,
) : RecipeRepository {

    suspend fun syncData() {
        val categories = apiService.fetchRecipeCategories()
        val units = apiService.fetchMeasurementUnits()

        recipeCategoryDao.insertCategories(categories.map { RecipeCategoryEntity(it.id, it.displayName) })

        units.map { measurementUnitDao.insertUnit(MeasurementUnitEntity(it.id!!, it.displayName, it.symbol)) }
    }

    suspend fun saveRecipe(recipe: Recipe) {
        if (recipe.isPrivate) {
            val recipeEntity = RecipeEntity(
                id = recipe.id,
                userID = recipe.userID,
                title = recipe.title,
                recipeCategoryId = recipe.recipeCategory.id,
                portionSize = recipe.portionSize,
                preparationTime = recipe.preparationTime,
                cookingTime = recipe.cookingTime,
                preparation = recipe.preparation,
                rating = recipe.rating,
                notes = recipe.notes,
                titleImage = recipe.titleImage,
                isPrivate = recipe.isPrivate,
                created = recipe.created,
                lastUpdated = recipe.lastUpdated
            )
            recipeDao.insertRecipe(recipeEntity)

            recipe.ingredients.forEach { ingredient ->

                ingredient.unit.id?.let {
                    val ingredientEntity = IngredientEntity(
                        id = ingredient.id,
                        value = ingredient.value,
                        name = ingredient.name,
                        formId = ingredient.form?.id,
                        unitId = it
                    )
                    ingredientDao.insertIngredient(ingredientEntity)
                }

            }
        }
    }

    override suspend fun getRecipes(): List<Recipe> {
        TODO("Not yet implemented")

    }

    override suspend fun getRecipeById(id: Int): Recipe? {
        TODO("Not yet implemented")

    }

    override suspend fun addRecipe(recipe: Recipe): Recipe? {
        TODO("Not yet implemented")
    }

    override suspend fun editRecipe(recipe: Recipe): Recipe? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecipe(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun syncRecipes() {
        TODO("Not yet implemented")
    }
}


