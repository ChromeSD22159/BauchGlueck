package data

import data.network.createHttpClient
import data.repositories.CountdownTimerRepository
import data.repositories.MedicationRepository
import data.repositories.WaterIntakeRepository
import data.repositories.WeightRepository
import data.remote.StrapiRecipeApiClient
import data.remote.syncManager.AppDataSyncManager
import data.repositories.FirebaseRepository
import data.repositories.MealPlanRepository
import data.repositories.MealRepository
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class Repository(
    val countdownTimerRepository: CountdownTimerRepository,
    val weightRepository: WeightRepository,
    val waterIntakeRepository: WaterIntakeRepository,
    val medicationRepository: MedicationRepository,
    val mealRepository: MealRepository,
    val mealPlanRepository: MealPlanRepository
) {
    val firebaseRepository: FirebaseRepository = FirebaseRepository()
    val recipeRepository: StrapiRecipeApiClient = StrapiRecipeApiClient()
    val appDataSyncManager: AppDataSyncManager = AppDataSyncManager()
}

