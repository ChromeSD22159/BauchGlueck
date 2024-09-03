package viewModel

import data.Repository
import data.local.entitiy.Weight
import data.model.DailyAverage
import data.model.MonthlyAverage
import data.model.WeeklyAverage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging

class WeightScreenViewModel(
    private val repository: Repository
): ViewModel(), BaseViewModel<WeightUiState, Weight> {
    override val scope: CoroutineScope = viewModelScope

    private val _uiState = MutableStateFlow(WeightUiState())
    override val uiState: StateFlow<WeightUiState> = _uiState.asStateFlow()


    private val _lastWeight = MutableStateFlow<Weight?>(null)
    val lastWeight: StateFlow<Weight?> = _lastWeight.asStateFlow()

    init {
        logging().info { "WeightViewModel init" }
        getAllItems()
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    override fun addItem(item: Weight) {
        viewModelScope.launch {
            repository.weightRepository.insertOrUpdate(item)
            _uiState.value.items.value = repository.weightRepository.getAll()
            syncDataWithRemote()
        }
    }

    override fun getAllItems() {
        viewModelScope.launch {
            _uiState.value.isLoading.value = true
            _uiState.value.items.value = repository.weightRepository.getAll()

            logging().info { "getAllWeights: ${repository.weightRepository.getAll()}" }

            _uiState.value.dailyAverage.value = repository.weightRepository.getAverageWeightLastDays()
            _uiState.value.isLoading.value = false
        }
    }

    override fun softDelete(item: Weight) {
        viewModelScope.launch {
            val weightToDelete = item.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.weightRepository.softDeleteMany(listOf(weightToDelete))
            _uiState.value.items.value = repository.weightRepository.getAll()
            syncDataWithRemote()
        }
    }

    override fun updateItemAndSyncRemote(item: Weight) {
        viewModelScope.launch {
            logging().info { "updateTimer: $item" }
            repository.weightRepository.insertOrUpdate(item)
            _uiState.value.items.value = repository.weightRepository.getAll()
            syncDataWithRemote()
        }
    }

    override fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.weightRepository.syncDataWithRemote()
        }
    }

    fun getLastWeight() {
        viewModelScope.launch {
           _lastWeight.value =  repository.weightRepository.getLastWeight()
        }
    }
}

data class WeightUiState(
    override val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val isFinishedSyncing: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val minimumDelay: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val hasError: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override var items: MutableStateFlow<List<Weight>> = MutableStateFlow(emptyList()),
    var dailyAverage: MutableStateFlow<List<DailyAverage>> = MutableStateFlow(emptyList()),
    var weeklyAverage: MutableStateFlow<List<WeeklyAverage>> = MutableStateFlow(emptyList()),
    var monthlyAverage: MutableStateFlow<List<MonthlyAverage>> = MutableStateFlow(emptyList()),
    var error: MutableStateFlow<String?> = MutableStateFlow(null),
): BaseUiState<Weight>