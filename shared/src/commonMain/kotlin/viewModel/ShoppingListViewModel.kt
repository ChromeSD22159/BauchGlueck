package viewModel

import data.Repository
import data.local.entitiy.ShoppingList
import data.model.GenerateShoppingListState
import data.remote.model.ShoppingListItem
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import util.UUID

class ShoppingListViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private val _shoppingLists = repository.shoppingListRepository.getShoppingLists()
    val shoppingLists = _shoppingLists.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker = _showDatePicker.asStateFlow()

    private val _inProgress = MutableStateFlow(GenerateShoppingListState.None)
    val inProgress = _inProgress.asStateFlow()

    private val _isAnimating = MutableStateFlow(false)
    val isAnimating = _isAnimating.asStateFlow()

    private val _currentShoppingList = MutableStateFlow<ShoppingList?>(null)
    val currentShoppingList = _currentShoppingList.asStateFlow()

    fun toggleDatePicker() {
        _showDatePicker.value = !_showDatePicker.value
    }

    fun startGenerateShoppingList(
        startDate: Long,
        endDate: Long
    ) {
        val timeZone = TimeZone.currentSystemDefault()
        val start = Instant.fromEpochMilliseconds(startDate).toLocalDateTime(timeZone).date
        val end = Instant.fromEpochMilliseconds(endDate).toLocalDateTime(timeZone).date

        viewModelScope.launch {
            val shoppingList = mutableListOf<ShoppingListItem>()

            val result = repository.mealPlanRepository.getMealPlanDaysWithSpotsForDateRange(
                start, end
            )

            _isAnimating.value = true
            _inProgress.value = GenerateShoppingListState.AnalyseMealPlans
            delay(2000)

            result.forEach { day ->
                day.spots.forEach { mealPlanSpot ->
                    mealPlanSpot.mealObject?.ingredients?.forEach {ingredient ->
                        val amountAsInt = ingredient.amount.toIntOrNull()
                        if (amountAsInt != null) {

                            val existingItem = shoppingList.find { it.name == ingredient.name }
                            if (existingItem != null) {
                                val currentAmount = existingItem.amount.toIntOrNull() ?: 0
                                existingItem.amount = (currentAmount + amountAsInt).toString()
                            } else {
                                shoppingList.add(
                                    ShoppingListItem(
                                        name = ingredient.name,
                                        amount = amountAsInt.toString(),
                                        unit = ingredient.unit,
                                        note = "",
                                    )
                                )
                            }
                        }
                    }
                }
            }

            _inProgress.value = GenerateShoppingListState.Calculate
            delay(2000)

            repository.shoppingListRepository.insertShoppingList(
                ShoppingList(
                    shoppingListId = UUID.randomUUID(),
                    name = start.toString() + end.toString(),
                    startDate = start.toString(),
                    endDate = end.toString(),
                    itemsString = Json.encodeToString(shoppingList)
                )
            )

            _inProgress.value = GenerateShoppingListState.Done
            _isAnimating.value = false

           /*
           repository.mealPlanRepository.getMealPlanDaysWithSpotsForDateRangeAsFlow(
               start, end
           ).collect { dayPlans ->

               val shoppingList = mutableListOf<ShoppingListItem>()
               _isAnimating.value = true
               _inProgress.value = GenerateShoppingListState.AnalyseMealPlans
               delay(2000)
               dayPlans.forEach { day ->
                   day.spots.forEach { mealPlanSpot ->
                       mealPlanSpot.mealObject?.ingredients?.forEach {ingredient ->
                           val amountAsInt = ingredient.amount.toIntOrNull()
                           if (amountAsInt != null) {

                               val existingItem = shoppingList.find { it.name == ingredient.name }
                               if (existingItem != null) {
                                   val currentAmount = existingItem.amount.toIntOrNull() ?: 0
                                   existingItem.amount = (currentAmount + amountAsInt).toString()
                               } else {
                                   shoppingList.add(
                                       ShoppingListItem(
                                           name = ingredient.name,
                                           amount = amountAsInt.toString(),
                                           unit = ingredient.unit,
                                           note = "",
                                       )
                                   )
                               }
                           }
                       }
                   }
               }

               _inProgress.value = GenerateShoppingListState.Calculate
               delay(2000)

               viewModelScope.launch {

                   repository.shoppingListRepository.insertShoppingList(
                       ShoppingList(
                           shoppingListId = UUID.randomUUID(),
                           name = start.toString() + end.toString(),
                           startDate = start.toString(),
                           endDate = end.toString(),
                           itemsString = Json.encodeToString(shoppingList)
                       )
                   )

                   _inProgress.value = GenerateShoppingListState.Done
                   _isAnimating.value = false
               }
           }
            */
       }
    }

    fun loadShoppingListByID(shoppingListId: String) {
        viewModelScope.launch {
            val result = repository.shoppingListRepository.getShoppingList(shoppingListId)

            if(result != null) {
                _currentShoppingList.value = result
            }
        }
    }

    fun clearCurrentShoppingList() {
        _currentShoppingList.value = null
    }

    fun markListAsComplete(shoppingList: ShoppingList) {
        viewModelScope.launch {
            repository.shoppingListRepository.insertShoppingList(
                shoppingList.copy(
                    isComplete = true,
                    updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun markListAsInComplete(shoppingList: ShoppingList) {
        viewModelScope.launch {
            repository.shoppingListRepository.insertShoppingList(
                shoppingList.copy(
                    isComplete = false,
                    updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun softDeleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            repository.shoppingListRepository.insertShoppingList(
                shoppingList.copy(
                    isDeleted = true,
                    updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
                )
            )
        }
    }

    fun updateIsCompleteFromShoppingListItem(shoppingListItemId: String, newState: Boolean) {
        val updatedShoppingList = currentShoppingList.value?.items?.map { item ->
            if (item.shoppingListItemId == shoppingListItemId) {
                item.copy(isComplete = newState)
            } else {
                item
            }
        } ?: emptyList()

        val new = _currentShoppingList.value.let { it?.copy(itemsString = Json.encodeToString(updatedShoppingList)) }

        viewModelScope.launch {
            if(new != null) {
                _currentShoppingList.emit(new)
            }

            currentShoppingList.value?.let {
                repository.shoppingListRepository.insertShoppingList(it)
            }
        }
    }
}