package de.frederikkohler.bauchglueck.ui.screens.authScreens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import data.model.RecipeCategory
import data.remote.StrapiApiClient
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import de.frederikkohler.bauchglueck.ui.screens.authScreens.SyncIconRotate
import de.frederikkohler.bauchglueck.ui.screens.authScreens.settingsSheet.SettingSheet
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import di.serverHost
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.lighthousegames.logging.logging
import util.debugJsonHelper
import util.onError
import util.onSuccess
import viewModel.FirebaseAuthViewModel
import viewModel.TimerScreenViewModel
import viewModel.WeightScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
) {
    val firebaseViewModel = koinViewModel<FirebaseAuthViewModel>()
    val timerScreenViewModel = koinViewModel<TimerScreenViewModel>()
    val weightScreenViewModel = koinViewModel<WeightScreenViewModel>()

    val isSyncInProgress by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var showSettingSheet by remember { mutableStateOf(false) }

    val dailyAverage by weightScreenViewModel.dailyAverage.collectAsState(initial = emptyList())
    val timers by timerScreenViewModel.allTimers.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    Row {
                        if (isSyncInProgress) {
                            SyncIconRotate()
                        }

                        Box(modifier = Modifier.padding(end = 16.dp)) {
                            RoundImageButton(R.drawable.ic_gear) {
                                showSettingSheet = true
                            }
                        }
                    }
                }
            )
        },
    ) { _ ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            Spacer(modifier = Modifier.height(90.dp))

            Button(onClick = { navController.navigate(Destination.Recipes.route) }) {
                Text(text = "Rezepte")
            }

            Button(onClick = {
                scope.launch {
                    val response = StrapiApiClient( serverHost ).createRecipe(RecipeCategory.HAUPTGERICHT)

                    response.onSuccess {
                        logging().info { "Success: ${it.name}" }

                        debugJsonHelper(it)
                    }
                    response.onError {
                        logging().info { "Error: $it" }
                    }
                }
            }) {
                Text(text = "Generate Recipe")
            }

            Button(onClick = {
                scope.launch {
                    Firebase.auth.signOut()
                    navController.navigate(Destination.Login.route)
                }
            }) {
                Text(text = "Logout")
            }

            HomeCalendarCard {
                scope.launch {
                    navController.navigate(Destination.Calendar.route)
                }
            }

            HomeWeightCard(
                dailyAverage,
            ) {
                scope.launch {
                    navController.navigate(Destination.Weight.route)
                }
            }

            HomeTimerCard(timers) {
                logging().info { "HomeTimerCard ${it.route}" }
                scope.launch {
                    navController.navigate(it.route)
                }
            }

            HomeWaterIntakeCard {
                scope.launch {
                    navController.navigate(Destination.WaterIntake.route)
                }
            }

            HomeMedicationCard {
                scope.launch {
                    navController.navigate(Destination.Medication.route)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            SettingSheet(
                showSettingSheet = showSettingSheet,
                onDismissRequest = {
                    showSettingSheet = false
                },
                onSignOut = {
                    scope.launch {
                        firebaseViewModel.onLogout()

                        delay(250)
                        showSettingSheet = false
                        delay(250)
                        if (firebaseViewModel.userFormState.value.currentUser == null) {
                            navController.navigate(Destination.Login.route)
                        }
                    }
                },
                firebaseAuthViewModel = firebaseViewModel,
            )
        }
    }
}