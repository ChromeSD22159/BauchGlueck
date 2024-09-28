package data

import data.repositories.CountdownTimerRepository
import data.repositories.MedicationRepository
import data.repositories.WaterIntakeRepository
import data.repositories.WeightRepository
import data.remote.StrapiRecipeApiClient
import data.remote.syncManager.AppDataSyncManager
import data.repositories.FirebaseRepository
import data.repositories.MealPlanRepository
import data.repositories.MealRepository
import data.repositories.NoteRepository

class Repository(
    val countdownTimerRepository: CountdownTimerRepository,
    val weightRepository: WeightRepository,
    val waterIntakeRepository: WaterIntakeRepository,
    val medicationRepository: MedicationRepository,
    val mealRepository: MealRepository,
    val mealPlanRepository: MealPlanRepository,
    val noteRepository: NoteRepository,
) {
    val firebaseRepository: FirebaseRepository = FirebaseRepository()
    val recipeRepository: StrapiRecipeApiClient = StrapiRecipeApiClient()
    val appDataSyncManager: AppDataSyncManager = AppDataSyncManager()
}

