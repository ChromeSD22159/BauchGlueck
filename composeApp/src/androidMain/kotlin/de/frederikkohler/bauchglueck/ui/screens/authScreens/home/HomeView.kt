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
import androidx.lifecycle.viewmodel.compose.viewModel
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.RoundImageButton
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import data.FirebaseConnection
import de.frederikkohler.bauchglueck.ui.screens.authScreens.settingsSheet.SettingSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import navigation.Screens

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    firebaseAuthViewModel: FirebaseAuthViewModel = viewModel(),
    navController: NavHostController
) {
    var showSettingSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                    Box(modifier = Modifier.padding(end = 16.dp)) {
                        RoundImageButton(R.drawable.icon_gear) {
                            showSettingSheet = true
                        }
                    }
                    SettingSheet(
                        showSettingSheet = showSettingSheet,
                        onDismissRequest = {
                            showSettingSheet = false
                            firebaseAuthViewModel.saveUserProfile(FirebaseConnection.Remote)
                        },
                        onSignOut = {
                            scope.launch {
                                firebaseAuthViewModel.signOut()
                                delay(250)
                                showSettingSheet = false
                                delay(250)
                                if (firebaseAuthViewModel.user.value == null) {
                                    navController.navigate(Screens.Login.route)
                                }
                            }
                        },
                        firebaseAuthViewModel = firebaseAuthViewModel,
                    )
                }
            )
        },
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            HomeCalendarCard() {
                scope.launch {
                    navController.navigate(Screens.Calendar.route)
                }
            }

            HomeWeightCard() {
                scope.launch {
                    navController.navigate(Screens.Weight.route)
                }
            }

            HomeTimerCard() {
                scope.launch {
                    navController.navigate(Screens.Timer.route)
                }
            }

            HomeWaterIntakeCard() {
                scope.launch {
                    navController.navigate(Screens.WaterIntake.route)
                }
            }
        }
    }
}