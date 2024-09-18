package viewModel

import data.Repository
import data.local.entitiy.MedicationWithIntakeDetails
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

class MedicationViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val medicationRepository = repository.medicationRepository
    private var scope = viewModelScope
    private val todayStart: Long
    private val todayEnd: Long

    init {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now().toLocalDateTime(timeZone)
        todayStart = now.date.atStartOfDayIn(timeZone).toEpochMilliseconds()
        todayEnd = todayStart + 86_400_000
    }

    val medicationsWithIntakeDetailsForToday: Flow<List<MedicationWithIntakeDetailsForToday>> = medicationRepository.getMedicationsWithIntakeTimesForToday()
        .map { medications ->

            logging().info { "Medications: $medications" }

            medications.map { medicationWithIntake ->
                val intakeTimesWithTodayStatus = medicationWithIntake.intakeTimesWithStatus.map { intakeTimeWithStatus ->
                    val todayStatuses = intakeTimeWithStatus.intakeStatuses.filter { status ->
                        status.date in todayStart..todayEnd
                    }
                    intakeTimeWithStatus.copy(intakeStatuses = todayStatuses)
                }
                medicationWithIntake.copy(intakeTimesWithStatus = intakeTimesWithTodayStatus)
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
