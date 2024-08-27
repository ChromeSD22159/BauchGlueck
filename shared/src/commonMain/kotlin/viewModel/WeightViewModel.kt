package viewModel

import data.Repository
import data.local.entitiy.Weight
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

    init {
        logging().info { "WeightViewModel init" }
        getAllCountdownWeights()
    }

    override fun onCleared() {
        super.onCleared()
        logging().info { "TimerViewModel onCleared" }
    }

    fun addWeight(value: Double) {

        val newWeight = Weight(
            weightId = generateDeviceId(),
            userId = Firebase.auth.currentUser!!.uid,
            value = value,
            isDeleted = false,
            createdAt = Clock.System.now().toEpochMilliseconds().toIsoDate(),
            updatedAt = Clock.System.now().toEpochMilliseconds().toIsoDate()
        )

        viewModelScope.launch {
            repository.weightRepository.insertOrUpdate(newWeight)
            _uiState.value = _uiState.value.copy(weights = repository.weightRepository.getAll())
            syncDataWithRemote()
        }
    }

    fun getAllCountdownWeights() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val weights = repository.weightRepository.getAll()
            _uiState.value = _uiState.value.copy(weights = weights)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun updateWeightAndSyncRemote(weight: Weight) {
        viewModelScope.launch {
            logging().info { "updateTimer: $weight" }
            repository.weightRepository.insertOrUpdate(weight)
            _uiState.value = _uiState.value.copy(weights = repository.weightRepository.getAll())
            syncDataWithRemote()
        }
    }

    fun softDeleteWeight(weight: Weight) {
        viewModelScope.launch {
            val weightCopy = weight.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.weightRepository.softDeleteMany(listOf(weightCopy))
            _uiState.value = _uiState.value.copy(weights = repository.weightRepository.getAll())
            syncDataWithRemote()
        }
    }


    fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.weightRepository.syncDataWithRemote()
        }
    }
}

data class WeightUiState(
    var isLoading: Boolean = false,
    var weights: List<Weight> = emptyList(),
    var error: String? = null
)