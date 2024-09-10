package viewModel

import data.Repository
import data.local.entitiy.MealWithCategories
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealViewModel(
    private val repository: Repository,
): ViewModel() {
    val localMeals: Flow<List<MealWithCategories>> = repository.mealRepository.getAllMealsMeals()

    val localMealCount: Flow<Int>
        get() = localMeals.map { it.size }


}