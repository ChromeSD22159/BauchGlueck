package data

import data.repositories.CountdownTimerRepository
import data.repositories.MedicationRepository
import data.repositories.WaterIntakeRepository
import data.repositories.WeightRepository

class Repository(
    val countdownTimerRepository: CountdownTimerRepository,
    val weightRepository: WeightRepository,
    val waterIntakeRepository: WaterIntakeRepository,
    val medicationRepository: MedicationRepository
)

