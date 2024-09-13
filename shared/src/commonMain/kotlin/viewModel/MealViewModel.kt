package viewModel

import data.Repository
import data.local.entitiy.MealWithCategories
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import util.DateRepository

class MealViewModel(
    private val repository: Repository,
): ViewModel() {
    private val localMeals: Flow<List<MealWithCategories>> = repository.mealRepository.getAllMealsMeals()
    private val dateRepository = DateRepository

    val days = dateRepository.getTheNextMonthDays

    val localMealCount: Flow<Int>
        get() = localMeals.map { it.size }

    private val _selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(dateRepository.today)
    val selectedDate: Flow<LocalDate> = _selectedDate

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

}