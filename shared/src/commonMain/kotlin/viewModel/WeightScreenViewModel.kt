package viewModel

import data.Repository
import data.local.entitiy.WaterIntake
import data.local.entitiy.Weight
import data.model.DailyAverage
import data.model.MonthlyAverage
import data.model.WeeklyAverage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import util.UUID

class WeightScreenViewModel : ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val scope: CoroutineScope = viewModelScope

    var allWeights: Flow<List<Weight>> = repository.weightRepository.getAllAsFlow()
    var lastWeight: Flow<Weight?> = repository.weightRepository.getLastWeight()

    private var _dailyAverage: MutableStateFlow<List<DailyAverage>> = MutableStateFlow(emptyList())
    var dailyAverage: StateFlow<List<DailyAverage>> = _dailyAverage.asStateFlow()

    init {
        logging().info { "WeightScreenViewModel init" }
        getAverageWeightLastDays()
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun addItem(item: Weight) {
        viewModelScope.launch {
            repository.weightRepository.insert(item)
            getAverageWeightLastDays()
            syncDataWithRemote()
        }
    }

    fun softDelete(item: Weight) {
        viewModelScope.launch {
            val weightToDelete = item.copy(isDeleted = true, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds())
            repository.weightRepository.softDeleteMany(listOf(weightToDelete))
            syncDataWithRemote()
        }
    }

    private fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.weightRepository.syncDataWithRemote()
        }
    }

    private fun getAverageWeightLastDays(days: Int = 7) {
        scope.launch {
            _dailyAverage.value = repository.weightRepository.getAverageWeightLastDays()
        }
    }
}

class WaterIntakeViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val scope: CoroutineScope = viewModelScope

    val user
        get() = repository.firebaseRepository.user

    val intakesToday = repository.waterIntakeRepository.getAllIntakesFromToday()

    fun insertIntake(value: Double = 0.25) {
        if (user == null) return

        scope.launch {
            val newIntake = WaterIntake(
                userId = user!!.uid,
                waterIntakeId = UUID.randomUUID(),
                value = value,
                updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
            )
            repository.waterIntakeRepository.insertOrUpdate(newIntake)
        }
    }
}