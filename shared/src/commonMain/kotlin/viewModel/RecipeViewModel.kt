package viewModel

import data.Repository
import data.local.entitiy.MealWithCategories
import data.remote.model.ApiRecipesResponse
import dev.gitlive.firebase.Firebase
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import util.onSuccess

class RecipeViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    val scope = viewModelScope

    private val _recipes: MutableStateFlow<List<ApiRecipesResponse>> = MutableStateFlow(emptyList())
    val recipes = _recipes.asStateFlow()

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val localMeals: Flow<List<MealWithCategories>> = repository.mealRepository.getAllMealsMeals()

    val localMealCount: Flow<Int>
        get() = localMeals.map { it.size }

    private var _selectedRecipe: MutableStateFlow<ApiRecipesResponse?> = MutableStateFlow(null)
    val selectedRecipe = _selectedRecipe.asStateFlow()

    fun fetchRecipes() {
        scope.launch {
            val result = repository.recipeRepository.getRecipesOverview(5)
            result.onSuccess {
                _recipes.value = it
            }
        }
    }

    fun searchRecipes(query: String) {
        val user = repository.firebaseRepository.user ?: return
        scope.launch {

            logging().info { query }

            val result = repository.recipeRepository.searchRecipes(
                query = query,
                userID = user.uid
            )

            result.onSuccess {
                _recipes.value = it
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedRecipe(recipe: ApiRecipesResponse) {
        _selectedRecipe.value = recipe
    }

    fun clearSelectedRecipe() {
        _selectedRecipe.value = null
    }

    fun resetRecipeList() {
        _recipes.value = emptyList()
    }
}