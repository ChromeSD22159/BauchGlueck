package viewModel

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import model.recipe.MeasurementUnit
import model.recipe.RecipeCategory
import data.network.RemoteBauchGlueckApiClient
import util.onSuccess

class SharedRecipeViewModel: ViewModel() {
    private val apiClient: RemoteBauchGlueckApiClient = RemoteBauchGlueckApiClient()

    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessing: Flow<Boolean> = _isProcessing.asStateFlow()

    private val _measureUnits = MutableStateFlow<List<MeasurementUnit>>(emptyList())
    val measureUnits: StateFlow<List<MeasurementUnit>>
        get() = _measureUnits.asStateFlow()

    private val _recipeCategories = MutableStateFlow<List<RecipeCategory>>(emptyList())
    val recipeCategories: StateFlow<List<RecipeCategory>>
        get() = _recipeCategories.asStateFlow()

    fun fetchMeasureUnits() {
        _isProcessing.value = true
        viewModelScope.launch {
            apiClient.getMeasurementUnits().onSuccess { measurementUnits ->
                _measureUnits.value = measurementUnits
            }
            _isProcessing.value = false
        }
    }

    fun fetchRecipeCategories() {
        _isProcessing.value = true
        viewModelScope.launch {
            apiClient.getRecipeCategories().onSuccess { recipeCategories ->
                _recipeCategories.value = recipeCategories
            }
            _isProcessing.value = false
        }
    }
}
