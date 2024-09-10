package data

import data.repositories.CountdownTimerRepository
import data.repositories.MedicationRepository
import data.repositories.WaterIntakeRepository
import data.repositories.WeightRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import data.remote.StrapiRecipeApiClient
import data.repositories.MealPlanRepository
import data.repositories.MealRepository

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

}

class FirebaseRepository() {
    val user = Firebase.auth.currentUser
}
