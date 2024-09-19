package viewModel

import data.Repository
import data.local.entitiy.WaterIntake
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import util.UUID

class WaterIntakeViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val scope: CoroutineScope = viewModelScope

    val user
        get() = repository.firebaseRepository.user

    val intakesToday = repository.waterIntakeRepository.getAllIntakesFromToday()

    fun insertIntake() {
        if (user == null) return

        scope.launch {
            val newIntake = WaterIntake(
                userId = user!!.uid,
                waterIntakeId = UUID.randomUUID(),
                value = 0.25,
                updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()
            )
            repository.waterIntakeRepository.insertOrUpdate(newIntake)

            syncDataWithRemote()
        }
    }

    fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.waterIntakeRepository.syncWaterIntakes()
        }
    }
}