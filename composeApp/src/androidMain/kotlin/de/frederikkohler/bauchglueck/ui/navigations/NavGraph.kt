package de.frederikkohler.bauchglueck.ui.navigations

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.frederikkohler.bauchglueck.ui.components.BackScaffold
import de.frederikkohler.bauchglueck.ui.components.GenerateRecipeWithGemini
import de.frederikkohler.bauchglueck.ui.screens.LaunchScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.meals.CalendarScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.home.HomeScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.medication.AddEditMedicationScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.medication.MedicationScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.AddEditTimerSheet
import de.frederikkohler.bauchglueck.ui.screens.authScreens.timer.TimerScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.addWeight.AddWeightScreen
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.showAllWeights.ShowAllWeights
import de.frederikkohler.bauchglueck.ui.screens.authScreens.weights.WeightScreen
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.LoginView
import de.frederikkohler.bauchglueck.ui.screens.publicScreens.RegisterView
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import di.serverHost
import org.koin.compose.KoinContext
import org.lighthousegames.logging.logging
import org.koin.androidx.compose.koinViewModel
import viewModel.RecipeViewModel
import viewModel.SyncWorkerViewModel
import de.frederikkohler.bauchglueck.ui.components.RecipeCard
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: FirebaseAuthViewModel,
    appContext: Context,
) {
    logging().info { "NavGraph" }

    val user = Firebase.auth.currentUser

    val syncWorker = koinViewModel<SyncWorkerViewModel>()

    val minimumDelay by syncWorker.uiState.value.minimumDelay.collectAsState()
    val isFinishedSyncing by syncWorker.uiState.value.isFinishedSyncing.collectAsState()
    val hasError by syncWorker.uiState.value.hasError.collectAsState()

    KoinContext {

        LaunchScreenDataSyncController(
            minimumDelay = minimumDelay,
            isFinishedSyncing = isFinishedSyncing,
            hasError = hasError,
            user = user,
            appContext = appContext,
            navController = navController
        )

        NavHost(navController = navController, startDestination = Destination.Launch.route) {
            launchScreen()

            login(navController)
            signUp(navController)
            home(navController, viewModel)
            calendar(navController)

            // WEIGHT
            weight(navController)
            addWeight(navController)
            showAllWeights(navController)

            // WATERINTAKE
            waterIntake(navController)
            // Medication
            medication(navController)
            addMedication(navController)
            editMedication(navController)

            // TIMER
            timerComposable(navController)
            addTimerComposable(navController)
            editTimerComposable(navController)

            recipesComposable(navController)
        }
    }
}

fun NavGraphBuilder.launchScreen() {
    composable(Destination.Launch.route) {
        LaunchScreen()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.calendar(navController: NavHostController) {
    composable(Destination.Calendar.route) {
        CalendarScreen(
            navController = navController
        )
    }
}

fun NavGraphBuilder.login(navController: NavHostController) {
    composable(Destination.Login.route) {
        LoginView( { navController.navigate(it.route) } )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.home(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.Home.route) {
        HomeScreen(
            firebaseAuthViewModel = firebaseAuthViewModel,
            navController = navController
        )
    }
}

fun NavGraphBuilder.signUp(navController: NavHostController) {
    composable(Destination.SignUp.route) {
        RegisterView( { navController.navigate(it.route) } )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.weight(navController: NavHostController) {
    composable(Destination.Weight.route) {
        WeightScreen(
            navController = navController,
            backNavigationDirection = Destination.Home
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addWeight(navController: NavHostController) {
    composable(
        route = Destination.AddWeight.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddWeightScreen(
            navController = navController,
            onDismiss = {
                navController.navigate(Destination.Weight.route)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.showAllWeights(navController: NavHostController) {
    composable(Destination.ShowAllWeights.route) {
        ShowAllWeights(
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.waterIntake(navController: NavHostController) {
    composable(Destination.WaterIntake.route) {
        BackScaffold(
            title = Destination.WaterIntake.title,
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.medication(navController: NavHostController){
    composable(Destination.Medication.route) {
        MedicationScreen(
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addMedication(navController: NavHostController){
    composable(
        route = Destination.AddMedication.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditMedicationScreen(
            navController = navController,
            currentMedication = null
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.editMedication(navController: NavHostController) {
    composable(
        route = Destination.EditMedication.route,
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() },
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
    ) {
        AddEditMedicationScreen(
            navController = navController,
            currentMedication = navController.currentBackStackEntry?.savedStateHandle?.get<String>("medicationId")
        )
    }
}

fun NavGraphBuilder.recipesComposable(navController: NavHostController) {
    composable(Destination.Recipes.route) {
        val recipeViewModel = koinViewModel<RecipeViewModel>()

        val localMealsCount by recipeViewModel.localMealCount.collectAsStateWithLifecycle(initialValue = 0)
        val localMealsState by recipeViewModel.localMeals.collectAsStateWithLifecycle(initialValue = emptyList())

        LaunchedEffect(localMealsCount) {
            logging().info { "localMealsState: $localMealsCount" }
        }

        Column(
            modifier = Modifier.padding(top = 55.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "Rezepte"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(localMealsState) { meal ->
                    RecipeCard(meal)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.editTimerComposable(navController: NavHostController) {
    composable(
        route = Destination.AddTimer.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditTimerSheet(
            navController = navController,
            currentCountdownTimer = null,
            onDismiss = {
                navController.navigate(Destination.Timer.route)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.addTimerComposable(navController: NavHostController) {
    composable(
        route = Destination.AddTimer.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditTimerSheet(
            navController = navController,
            currentCountdownTimer = null,
            onDismiss = {
                navController.navigate(Destination.Timer.route)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.timerComposable(navController: NavHostController) {
    composable(Destination.Timer.route) {
        TimerScreen(
            navController = navController,
        )
    }
}



