package viewModel

import data.Repository
import data.local.entitiy.MedicationWithIntakeDetails
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import data.model.medication.MedicationDate
import data.model.medication.MedicationHistory
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import util.DateRepository
import util.startEndTodayIn

class MedicationViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val medicationRepository = repository.medicationRepository
    private var scope = viewModelScope
    private val today = DateRepository.startEndTodayCurrentTimeZone()

    private val _medicationsWithIntakeDetailsForToday = MutableStateFlow<List<MedicationWithIntakeDetailsForToday>>(emptyList())
    val medicationsWithIntakeDetailsForToday: StateFlow<List<MedicationWithIntakeDetailsForToday>> = _medicationsWithIntakeDetailsForToday

    private val _medicationHistory = MutableStateFlow<List<MedicationHistory>>(emptyList())
    val medicationHistory: StateFlow<List<MedicationHistory>> = _medicationHistory.asStateFlow()

    init {
        loadMedicationsWithIntakeDetailsForToday()

        scope.launch {
            _medicationsWithIntakeDetailsForToday.collect {
                loadMedicationHistoryForLastWeeks()
            }
        }
    }

    private fun loadMedicationsWithIntakeDetailsForToday() {
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

    private fun loadMedicationHistoryForLastWeeks(weeks: Int = 16) {
        viewModelScope.launch {
            val res = repository.medicationRepository.getMedicationsWithIntakeTimes()

            val currentTime = Clock.System.now()
            val timeZone = TimeZone.currentSystemDefault() // TimeZone.UTC

            // Calculate the Sunday of the current week
            val weekDay = currentTime.toLocalDateTime(timeZone).dayOfWeek.ordinal
            val daysTillSunday = 6 - weekDay
            val sunday = currentTime.plus(daysTillSunday, unit = DateTimeUnit.DAY, timeZone)

            // Iterate through each medication and create history data for the last 12 weeks (84 days)
            _medicationHistory.value = res.map { medicationWithIntakeDetailsForToday ->

                val medication = medicationWithIntakeDetailsForToday.medication.name
                val medicationDateData = mutableListOf<List<MedicationDate>>()
                val dosage = medicationWithIntakeDetailsForToday.medication.dosage

                // Iterate over 12 weeks
                repeat(weeks) { weekIndex ->
                    val weekData = mutableListOf<MedicationDate>()

                    // Iterate over 7 days for each week
                    repeat(7) { dayIndex ->
                        val day = sunday.minus(weekIndex * 7L + dayIndex, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone)
                        val startEndOfDay = day.startEndTodayIn()

                        // Count total intake times for the day
                        val intakeCount = medicationWithIntakeDetailsForToday.intakeTimesWithStatus.flatMap { intakeTimeWithStatus ->
                            intakeTimeWithStatus.intakeStatuses.filter { status ->
                                status.date in startEndOfDay.start..startEndOfDay.end
                            }.map { it.isTaken }
                        }.count { it }

                        // Calculate the percentage for the day
                        val totalIntakeTimes = medicationWithIntakeDetailsForToday.intakeTimesWithStatus.size
                        val intakePercentage = if (totalIntakeTimes > 0) {
                            (intakeCount.toDouble() / totalIntakeTimes) * 100
                        } else {
                            0.0
                        }

                        // Add the day and intake percentage to the week's data
                        weekData.add(MedicationDate(day, startEndOfDay, intakePercentage))
                    }

                    // Add the week's data to the medication date data
                    medicationDateData.add(weekData)
                }

                // Return the medication history data for 12 weeks
                MedicationHistory(medication, dosage, medicationDateData)
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