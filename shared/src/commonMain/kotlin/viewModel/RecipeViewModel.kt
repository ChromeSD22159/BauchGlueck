package viewModel

import data.Repository
import data.local.entitiy.MealWithCategories
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecipeViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    val scope = viewModelScope

    private val _recipes: MutableStateFlow<List<MealWithCategories>> = MutableStateFlow(emptyList())
    val recipes = _recipes.asStateFlow()

    private val _foundRecipes: MutableStateFlow<List<MealWithCategories>> = MutableStateFlow(emptyList())
    val foundRecipes = _foundRecipes.asStateFlow()

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var _selectedRecipe: MutableStateFlow<MealWithCategories?> = MutableStateFlow(null)
    val selectedRecipe = _selectedRecipe.asStateFlow()

    private val _recipeForCategories: MutableStateFlow<List<MealWithCategories>> = MutableStateFlow(emptyList())
    val recipeForCategories = _recipeForCategories.asStateFlow()

    init {
        viewModelScope.launch {
            val localMeals: Flow<List<MealWithCategories>> = repository.mealRepository.getAllMealsMeals()

            localMeals.collect { meals ->
                _recipes.emit(meals)
                _recipeForCategories.emit(meals)

                searchRecipes("")
            }
        }

        viewModelScope.launch {
            searchQuery.collectLatest { query ->
                searchRecipes(query)
            }
        }
    }

    private fun searchRecipes(query: String) {
        val user = repository.firebaseRepository.user ?: return
        scope.launch {
            val result = recipes.value.filter {
                (it.meal.name.contains(query) && !it.meal.isPrivate) || (it.meal.name.contains(query) && it.meal.isPrivate && it.meal.userId == user.uid)
            }

            if (query == "") {
                _foundRecipes.value = recipes.value
            } else {
                _foundRecipes.value = result
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedRecipe(recipe: MealWithCategories) {
        _selectedRecipe.value = recipe
    }

    fun clearSelectedRecipe() {
        _selectedRecipe.value = null
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }
}