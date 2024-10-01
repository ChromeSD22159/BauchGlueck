package viewModel

import data.Repository
import data.local.entitiy.CountdownTimer
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import data.model.NextMedication
import data.model.WeeklyAverage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import util.DateRepository
import util.debugJsonHelper

class HomeViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val dateRepository: DateRepository = DateRepository
    private val today = dateRepository.startEndTodayCurrentTimeZone()
    private val currentDate = dateRepository.getCurrentDate()

    private var _medicationsWithIntakeDetailsForToday = MutableStateFlow<List<MedicationWithIntakeDetailsForToday>>(emptyList())
    val medicationsWithIntakeDetailsForToday: StateFlow<List<MedicationWithIntakeDetailsForToday>> = _medicationsWithIntakeDetailsForToday.asStateFlow()

    private val _medicationListNotTakenToday = MutableStateFlow<List<MedicationWithIntakeDetailsForToday>>(emptyList())
    val medicationListNotTakenToday: StateFlow<List<MedicationWithIntakeDetailsForToday>> = _medicationListNotTakenToday.asStateFlow()

    private val _weeklyAverage = MutableStateFlow<List<WeeklyAverage>>(emptyList())
    val weeklyAverage: StateFlow<List<WeeklyAverage>> = _weeklyAverage.asStateFlow()

    private val _timers = repository.countdownTimerRepository.getAllAsFlow()
    val allTimers: StateFlow<List<CountdownTimer>> = _timers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), emptyList())

    private val _nextMedicationIntake = MutableStateFlow<NextMedication?>(null)
    val nextMedicationIntake: StateFlow<NextMedication?> = _nextMedicationIntake.asStateFlow()

    init {
        loadMedicationsWithIntakeDetailsForToday()
        loadMedicationListNotTakenToday()
        getAverageWeightWeeks(49)
        observeAndFindNextMedication()
    }

    private fun loadMedicationListNotTakenToday() {
        viewModelScope.launch {
            val todayStart = today.start // Anfang des heutigen Tages
            val todayEnd = today.end // Ende des heutigen Tages

            // Lade alle Medikamente mit ihren Intake-Zeiten und Status
            val list = repository.medicationRepository.getMedicationsWithIntakeTimes()

            // Filtere und transformiere die Liste für heute
            val newList = list.mapNotNull { medicationWithIntakes ->
                // Filtere die IntakeTimes, um nur die zu behalten, die noch nicht genommen wurden
                val intakeTimesForToday = medicationWithIntakes.intakeTimesWithStatus.mapNotNull { intakeTimeWithStatus ->
                    // Behalte IntakeTimes in der Liste wenn:
                    // 1. Kein Status existiert (intakeStatuses.isEmpty())
                    // 2. Status existiert, aber isTaken ist false
                    val notTakenOrNoStatus = intakeTimeWithStatus.intakeStatuses.filter { status ->
                        status.date in todayStart..todayEnd || !status.isTaken
                    }

                    if (intakeTimeWithStatus.intakeStatuses.isEmpty() || notTakenOrNoStatus.isNotEmpty()) {
                        // Füge IntakeTime hinzu, wenn kein Status existiert oder nicht genommene Status existieren
                        intakeTimeWithStatus.copy(intakeStatuses = notTakenOrNoStatus)
                    } else {
                        // Entferne IntakeTime, wenn alle Status `isTaken` sind
                        null
                    }
                }

                // Wenn es IntakeTimes gibt, die noch nicht genommen wurden oder keinen Status haben
                if (intakeTimesForToday.isNotEmpty()) {
                    // Erstelle einen neuen MedicationWithIntakeDetailsForToday Eintrag
                    MedicationWithIntakeDetailsForToday(
                        medication = medicationWithIntakes.medication,
                        intakeTimesWithStatus = intakeTimesForToday
                    )
                } else {
                    null
                }
            }

            // Sortiere die Liste nach der nächsten IntakeTime
            val sortedList = newList.sortedBy { medication ->
                // Finde die früheste IntakeTime in den nicht erfüllten Zeiten
                medication.intakeTimesWithStatus.minOf { intakeTimeWithStatus ->
                    val (hour, minute) = intakeTimeWithStatus.intakeTime.intakeTime.split(":").map { it.toInt() }
                    hour * 60 + minute // Konvertiere Zeit zu Minuten, um einfach zu vergleichen
                }
            }

            // Setze die neue gefilterte und sortierte Liste
            _medicationListNotTakenToday.value = sortedList
        }
    }

    private fun loadMedicationsWithIntakeDetailsForToday() {
        viewModelScope.launch {
            _medicationsWithIntakeDetailsForToday.value = repository.medicationRepository.getMedicationsWithIntakeTimes()
        }
    }

    private fun getAverageWeightWeeks(days: Int = 49) {
        viewModelScope.launch {
            val list = repository.weightRepository.getAverageWeightLastDays(days = days)

            val chunkedList = list.chunked(7)

            _weeklyAverage.value = chunkedList.map { dayList ->
                val weightList = dayList.filter { it.avgValue > 0.0 }
                WeeklyAverage(
                    week = dayList.first().date,
                    avgValue = if (weightList.isEmpty()) 0.0 else weightList.sumOf { it.avgValue } / weightList.size
                )
            }
        }
    }

    private fun observeAndFindNextMedication() {
        viewModelScope.launch {
            try {
                medicationsWithIntakeDetailsForToday.collect { medicationWithIntake ->

                    if(medicationWithIntake.isEmpty()) return@collect

                    val medication = medicationWithIntake
                        .flatMap {
                            it.intakeTimesWithStatus.mapNotNull { intakeTimeWithStatus ->
                                val (hour, minute) = intakeTimeWithStatus.intakeTime.intakeTime.split(":").map { string -> string.toInt() }

                                val status = intakeTimeWithStatus.intakeStatuses.filter { intakeStatus ->
                                    intakeStatus.date in (today.start + 1)..<today.end
                                }

                                if(status.isEmpty()) {
                                    NextMedication(
                                        medication = it.medication,
                                        intakeTime = dateRepository.createLocalDateTime(
                                            year = currentDate.year,
                                            month = currentDate.monthNumber,
                                            day = currentDate.dayOfMonth,
                                            hour = hour,
                                            minute = minute,
                                            second = 0,
                                            nanosecond = 0
                                        )
                                    )
                                } else {
                                    null
                                }
                            }
                        }

                    medication.minByOrNull { it.intakeTime }!!.let { nextMedication ->
                        _nextMedicationIntake.value = nextMedication
                    }

                }
            } catch (e: Exception) {

            }

        }
    }
}