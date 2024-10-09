package viewModel

import data.Repository
import data.local.entitiy.Meal
import data.local.entitiy.MealPlanDay
import data.local.entitiy.MealPlanDayWithSpots
import data.local.entitiy.MealPlanSpot
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import util.DateRepository
import util.UUID

class MealPlanViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val mealPlanRepository = repository.mealPlanRepository
    private val dateRepository = DateRepository

    val calendarDays = dateRepository.getTheNextMonthDaysLocalDate

    private val mealPlans = repository.mealPlanRepository.getMealPlanDaysWithSpotsForDateRangeAsFlow(calendarDays.first(),calendarDays.last())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())

    private val _mealPlanForSelectedDate: MutableStateFlow<MealPlanDayWithSpots?> = MutableStateFlow(null)
    val mealPlanForSelectedDate: StateFlow<MealPlanDayWithSpots?> = _mealPlanForSelectedDate.asStateFlow()

    private val _selectedDate: MutableStateFlow<LocalDate> = MutableStateFlow(dateRepository.today) // 2024-10-02
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _countOfMealsPerDay: MutableStateFlow<List<Int>> = MutableStateFlow(emptyList())
    val countOfMealsPerDay: StateFlow<List<Int>> = _countOfMealsPerDay.asStateFlow()

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    init {
        viewModelScope.launch {
            mealPlans.collect { dayPlans ->
                loadMealPlanForDate(selectedDate.value)

                _countOfMealsPerDay.value = calculateTotalMealsForDate(
                    dateList = calendarDays,
                    planList = dayPlans
                )
            }
        }

        viewModelScope.launch {
            _selectedDate.collect {
                loadMealPlanForDate(it)
            }
        }
    }

    fun addToMealPlan(mealId: String, forDate: LocalDate) {
        val user = repository.firebaseRepository.user ?: return

        viewModelScope.launch {

            val foundModel = repository.mealRepository.getMealWithCategoryById(mealId) ?: return@launch
            val currentTimestamp = Clock.System.now().toEpochMilliseconds()

            // Find if a MealPlanDay already exists for this date
            val foundMealPlan = repository.mealPlanRepository.getMealPlanDayWithSpotsForDate(forDate.toString())
            if (foundMealPlan != null) {
                // Create and add MealPlanSpot to the existing MealPlanDay
                val mealPlanSpot = MealPlanSpot(
                    mealPlanSpotId = UUID.randomUUID(),
                    mealPlanDayId = foundMealPlan.mealPlanDay.mealPlanDayId,
                    mealId = foundModel.meal.mealId,
                    userId = user.uid,
                    timeSlot = forDate.toString(),
                    isDeleted = false,
                    meal = foundModel.meal.toJsonString,
                    updatedAtOnDevice = currentTimestamp
                )
                mealPlanRepository.insertMealPlanSpot(mealPlanSpot)
                logging().d { "MealPlanSpot inserted for existing MealPlanDay: $mealPlanSpot" }

            } else {
                // Create new MealPlanDay
                val mealPlanDay = MealPlanDay(
                    mealPlanDayId = UUID.randomUUID(),
                    userId = user.uid,
                    date = forDate.toString(),
                    isDeleted = false,
                    updatedAtOnDevice = currentTimestamp
                )
                mealPlanRepository.insertMealPlanDay(mealPlanDay)
                logging().d { "New MealPlanDay created: $mealPlanDay" }

                // Create and add MealPlanSpot to the new MealPlanDay
                val mealPlanSpot = MealPlanSpot(
                    mealPlanSpotId = UUID.randomUUID(),
                    mealPlanDayId = mealPlanDay.mealPlanDayId,
                    mealId = foundModel.meal.mealId,
                    userId = user.uid,
                    timeSlot = forDate.toString(),
                    isDeleted = false,
                    meal = foundModel.meal.toJsonString,
                    updatedAtOnDevice = currentTimestamp
                )
                mealPlanRepository.insertMealPlanSpot(mealPlanSpot)
                logging().d { "MealPlanSpot inserted for new MealPlanDay: $mealPlanSpot" }
            }

            // Reload the meal plans and sync with the remote
            loadMealPlanForDate(forDate)
            syncDataWithRemote()
        }
    }

    fun removeFromMealPlan(mealPlanDayId: String, mealPlanSpotId: String) {
        viewModelScope.launch {
            val foundMealPlan = mealPlans.value.find { it.mealPlanDay.mealPlanDayId == mealPlanDayId }

            if (foundMealPlan != null) {
                val updatedMealPlan = foundMealPlan.copy()
                val foundSpotIndex = updatedMealPlan.spots.indexOfFirst { it.mealPlanSpotId == mealPlanSpotId }

                if (foundSpotIndex != -1) {
                    val updatedSpot = updatedMealPlan.spots[foundSpotIndex].copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
                    mealPlanRepository.insertMealPlanSpot(updatedSpot)

                    logging().info { "Updated MealPlan: $updatedMealPlan" }
                }
            }

            loadMealPlanForDate(selectedDate.value)
            syncDataWithRemote()
        }
    }

    private fun loadMealPlanForDate(date: LocalDate) {
        viewModelScope.launch {
            val data = mealPlans.value.find { LocalDate.parse(it.mealPlanDay.date) == date }
            data?.let { dayPlan ->
                _mealPlanForSelectedDate.value = MealPlanDayWithSpots(
                    mealPlanDay = dayPlan.mealPlanDay,
                    spots = dayPlan.spots.filter { !it.isDeleted }
                )
            }
        }
    }

    private fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.mealPlanRepository.syncMealPlan()
        }
    }

    private fun calculateTotalMealsForDate(dateList: List<LocalDate>, planList: List<MealPlanDayWithSpots>): List<Int> {
         return dateList.map { date ->
            val planDay = planList.find { it.mealPlanDay.date == date.toString() }
            val count = planDay?.spots?.count { spot ->
                val mealJson = spot.meal
                val meal = mealJson?.let { string ->
                    Json.decodeFromString<Meal>(string)
                }

                val mealIsValid = meal != null && !meal.isDeleted
                val spotIsValid = !spot.isDeleted

                if (mealIsValid && spotIsValid) {
                    logging().info { "Valid meal and spot for spot: ${spot.mealPlanSpotId}" }
                    true
                } else {
                    logging().info { "Invalid meal or spot for spot: ${spot.mealPlanSpotId}, mealIsValid: $mealIsValid, spotIsValid: $spotIsValid" }
                    false
                }
            } ?: 0
            count
        }
    }
}
