package viewModel

import data.Repository
import data.local.entitiy.MealWithCategories
import data.remote.model.ApiRecipesResponse
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import util.onSuccess

class RecipeViewModel(
    private val repository: Repository,
): ViewModel() {
    private val scope = viewModelScope

    private val _recipes: MutableStateFlow<List<ApiRecipesResponse>> = MutableStateFlow(emptyList())
    val recipes = _recipes.asStateFlow()

    val localMeals: Flow<List<MealWithCategories>> = repository.mealRepository.getAllMealsMeals()

    val localMealCount: Flow<Int>
        get() = localMeals.map { it.size }

    fun fetchRecipes() {
        scope.launch {
            val result = repository.recipeRepository.getRecipesOverview(5)
            result.onSuccess {
                _recipes.value = it
            }
        }
    }
}