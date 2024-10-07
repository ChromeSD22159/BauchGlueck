package viewModel

import data.Repository
import data.local.entitiy.ShoppingList
import data.remote.model.ShoppingListItem
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShoppingListViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()

    private val _shoppingLists = repository.shoppingListRepository.getShoppingLists()
    val shoppingLists = _shoppingLists.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())

    private val _showDatePicker = MutableStateFlow(false)
    val showDatePicker = _showDatePicker.asStateFlow()

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
           repository.mealPlanRepository.getMealPlanDaysWithSpotsForDateRangeAsFlow(
               start, end
           ).collect { dayPlans ->

               val shoppingList = mutableListOf<ShoppingListItem>()

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

               insertShoppingList(
                   start,
                   end,
                   shoppingList
               )
           }
       }
    }

    private fun insertShoppingList(
        start: LocalDate,
        end: LocalDate,
        shoppingList: List<ShoppingListItem>
    ) {
        viewModelScope.launch {
            repository.shoppingListRepository.insertShoppingList(
                ShoppingList(
                    startDate = start.toString(),
                    endDate = end.toString(),
                    itemsString = shoppingList.toString()
                )
            )
        }
    }
}