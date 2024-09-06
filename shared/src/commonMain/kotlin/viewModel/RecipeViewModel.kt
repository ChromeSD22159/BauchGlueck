package viewModel

import data.Repository
import data.remote.model.ApiRecipesResponse
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import util.onSuccess

class RecipeViewModel(
    private val repository: Repository,
): ViewModel() {
    private val scope = viewModelScope

    private val _recipes: MutableStateFlow<List<ApiRecipesResponse>> = MutableStateFlow(emptyList())
    val recipes = _recipes.asStateFlow()

    fun fetchRecipes() {
        scope.launch {
            val result = repository.recipeRepository.getRecipesOverview(5)
            result.onSuccess {
                _recipes.value = it
            }
        }
    }
}