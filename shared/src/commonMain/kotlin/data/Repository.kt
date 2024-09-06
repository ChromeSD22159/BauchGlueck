package data

import data.repositories.CountdownTimerRepository
import data.repositories.MedicationRepository
import data.repositories.WaterIntakeRepository
import data.repositories.WeightRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import viewModel.StrapiRecipeApiClient

class Repository(
    val countdownTimerRepository: CountdownTimerRepository,
    val weightRepository: WeightRepository,
    val waterIntakeRepository: WaterIntakeRepository,
    val medicationRepository: MedicationRepository
) {
    val firebaseRepository: FirebaseRepository = FirebaseRepository()
    val recipeRepository: StrapiRecipeApiClient = StrapiRecipeApiClient()
}

class FirebaseRepository() {
    val user = Firebase.auth.currentUser
}
