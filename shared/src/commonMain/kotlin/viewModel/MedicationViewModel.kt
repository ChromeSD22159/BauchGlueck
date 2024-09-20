package viewModel

import data.Repository
import data.local.entitiy.MedicationWithIntakeDetails
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import util.DateRepository

class MedicationViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val medicationRepository = repository.medicationRepository
    private var scope = viewModelScope
    private val today = DateRepository.startEndTodayCurrentTimeZone()

    private val _medicationsWithIntakeDetailsForToday = MutableStateFlow<List<MedicationWithIntakeDetailsForToday>>(emptyList())
    val medicationsWithIntakeDetailsForToday: StateFlow<List<MedicationWithIntakeDetailsForToday>> = _medicationsWithIntakeDetailsForToday

    init {
        loadMedicationsWithIntakeDetailsForToday()
    }

    fun loadMedicationsWithIntakeDetailsForToday() {
        viewModelScope.launch {
            medicationRepository.getMedicationsWithIntakeTimesForToday()
                .map { medications ->
                    medications.map { medicationWithIntake ->
                        val intakeTimesWithTodayStatus = medicationWithIntake.intakeTimesWithStatus.map { intakeTimeWithStatus ->
                            val todayStatuses = intakeTimeWithStatus.intakeStatuses.filter { status ->
                                status.date in today.start..today.end
                            }
                            intakeTimeWithStatus.copy(intakeStatuses = todayStatuses)
                        }
                        medicationWithIntake.copy(intakeTimesWithStatus = intakeTimesWithTodayStatus)
                    }
                }
                .collect {
                    _medicationsWithIntakeDetailsForToday.value = it
                }
        }
    }


    fun insertMedicationWithIntakeDetails(medicationWithIntakeDetails: MedicationWithIntakeDetails) {
        scope.launch {
            medicationRepository.insertMedicationWithIntakeDetails(medicationWithIntakeDetails)

            syncDataWithRemote()
        }
    }

    fun getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId: String): Flow<MedicationWithIntakeDetailsForToday> {
        return medicationRepository.getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId)
    }

    private fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.medicationRepository.syncDataWithRemote()
        }
    }
}
