package viewModel

import data.Repository
import data.local.entitiy.Weight
import data.model.DailyAverage
import data.model.MonthlyAverage
import data.model.WeeklyAverage
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging
import util.generateDeviceId
import util.toIsoDate

class WeightViewModel(
    private val repository: Repository
): ViewModel() {

    private val _uiState = MutableStateFlow(WeightUiState())
    val uiState: StateFlow<WeightUiState> = _uiState.asStateFlow()

    private val _lastWeight = MutableStateFlow<Weight?>(null)
    val lastWeight: StateFlow<Weight?> = _lastWeight.asStateFlow()

    init {
        logging().info { "WeightViewModel init" }
        getAllCountdownWeights()
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    fun getCardWeights() {
        viewModelScope.launch {
            _uiState.value.dailyAverage.value = repository.weightRepository.getAverageWeightLastDays()
            _uiState.value.weeklyAverage.value = repository.weightRepository.getAverageWeightLastWeeks()
            _uiState.value.monthlyAverage.value = repository.weightRepository.getAverageWeightLastMonths()
        }
    }

    fun addWeight(value: Double) {

        val newWeight = Weight(
            weightId = generateDeviceId(),
            userId = Firebase.auth.currentUser!!.uid,
            value = value,
            isDeleted = false,
            weighed = Clock.System.now().toEpochMilliseconds().toIsoDate(),
            createdAt = Clock.System.now().toEpochMilliseconds().toIsoDate(),
            updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate()
        )

        viewModelScope.launch {
            repository.weightRepository.insertOrUpdate(newWeight)
            _uiState.value.weights.value = repository.weightRepository.getAll()
            syncDataWithRemote()
        }
    }

    fun getAllCountdownWeights() {
        viewModelScope.launch {
            _uiState.value.isLoading.value = true
            _uiState.value.weights.value = repository.weightRepository.getAll()
            _uiState.value.isLoading.value = false
        }
    }

    fun updateWeightAndSyncRemote(weight: Weight) {
        viewModelScope.launch {
            logging().info { "updateTimer: $weight" }
            repository.weightRepository.insertOrUpdate(weight)
            _uiState.value.weights.value = repository.weightRepository.getAll()
            syncDataWithRemote()
        }
    }

    fun softDeleteWeight(weight: Weight) {
        viewModelScope.launch {
            val weightCopy = weight.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.weightRepository.softDeleteMany(listOf(weightCopy))
            _uiState.value.weights.value = repository.weightRepository.getAll()
            syncDataWithRemote()
        }
    }

    fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.weightRepository.syncDataWithRemote()
        }
    }

    fun getLastWeight() {
        viewModelScope.launch {
           _lastWeight.value =  repository.weightRepository.getLastWeight()

            logging().info { "lastWeight: ${repository.weightRepository.getLastWeight()}" }
        }
    }
}

data class WeightUiState(
    var isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false),
    var weights: MutableStateFlow<List<Weight>> = MutableStateFlow(emptyList()),
    var dailyAverage: MutableStateFlow<List<DailyAverage>> = MutableStateFlow(emptyList()),
    var weeklyAverage: MutableStateFlow<List<WeeklyAverage>> = MutableStateFlow(emptyList()),
    var monthlyAverage: MutableStateFlow<List<MonthlyAverage>> = MutableStateFlow(emptyList()),
    var error: MutableStateFlow<String?> = MutableStateFlow(null),
)