package ui.navigations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ui.components.BackScaffold
import ui.screens.LaunchScreen
import ui.screens.authScreens.meals.CalendarScreen
import ui.screens.authScreens.home.HomeScreen
import ui.screens.authScreens.medication.AddEditMedicationScreen
import ui.screens.authScreens.medication.MedicationScreen
import ui.screens.authScreens.timer.AddEditTimerSheet
import ui.screens.authScreens.timer.TimerScreen
import ui.screens.authScreens.weights.addWeight.AddWeightScreen
import ui.screens.authScreens.weights.showAllWeights.ShowAllWeights
import ui.screens.authScreens.weights.WeightScreen
import ui.screens.publicScreens.LoginView
import ui.screens.publicScreens.RegisterView
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import org.koin.compose.KoinContext
import org.lighthousegames.logging.logging
import viewModel.RecipeViewModel
import viewModel.SyncWorkerViewModel
import ui.components.RecipeCard
import ui.screens.authScreens.settings.SettingScreen
import ui.screens.publicScreens.ForgotPasswordScreen
import viewModel.FirebaseAuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    navController: NavHostController,
    makeToast: (String) -> Unit,
) {
    logging().info { "NavGraph" }

    val user = Firebase.auth.currentUser

    val syncWorker = viewModel<SyncWorkerViewModel>()
    val firebaseAuthViewModel = viewModel<FirebaseAuthViewModel>()

    val minimumDelay by syncWorker.uiState.value.minimumDelay.collectAsState()
    val isFinishedSyncing by syncWorker.uiState.value.isFinishedSyncing.collectAsState()
    val hasError by syncWorker.uiState.value.hasError.collectAsState()

    KoinContext {

        LaunchScreenDataSyncController(
            minimumDelay = minimumDelay,
            isFinishedSyncing = isFinishedSyncing,
            hasError = hasError,
            user = user,
            navController = navController
        ) {
            makeToast(it)
        }

        NavHost(navController = navController, startDestination = Destination.Launch.route) {
            launchScreen()

            login(navController, firebaseAuthViewModel)
            forgotPassword(navController, firebaseAuthViewModel)
            signUp(navController, firebaseAuthViewModel)
            home(navController)
            calendar(navController, firebaseAuthViewModel)

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

            settingsComposable(navController, firebaseAuthViewModel)
        }
    }
}

fun NavGraphBuilder.launchScreen() {
    composable(Destination.Launch.route) {
        LaunchScreen()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.calendar(
    navController: NavHostController,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    composable(Destination.Calendar.route) {
        CalendarScreen(
            navController = navController,
            backNavigationDirection = Destination.Home,
            firebaseAuthViewModel = firebaseAuthViewModel
        )
    }
}

fun NavGraphBuilder.login(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.Login.route) {
        LoginView(navController, firebaseAuthViewModel)
    }
}

fun NavGraphBuilder.forgotPassword(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.ForgotPassword.route) {
        ForgotPasswordScreen(
            firebaseViewModel = firebaseAuthViewModel,
            onNavigate = { navController.navigate(it.route) }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.home(navController: NavHostController) {
    composable(Destination.Home.route) {
        HomeScreen(
            navController = navController
        )
    }
}

fun NavGraphBuilder.signUp(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.SignUp.route) {
        RegisterView(firebaseAuthViewModel) { navController.navigate(it.route) }
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
        val recipeViewModel = viewModel<RecipeViewModel>()

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
        route = Destination.EditTimer.route,
        enterTransition = { NavigationTransition.slideInWithFadeToTopAnimation() },
        exitTransition = { NavigationTransition.slideOutWithFadeToTopAnimation() }
    ) {
        AddEditTimerSheet(
            navController = navController,
            currentCountdownTimer = navController.currentBackStackEntry?.savedStateHandle?.get<String>("timerId"),
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

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.settingsComposable(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.Settings.route) {
        BackScaffold(
            title = Destination.Settings.title,
            navController = navController,
        ) {
            SettingScreen(
                navController,
                firebaseAuthViewModel = firebaseAuthViewModel
            )
        }
    }
}


