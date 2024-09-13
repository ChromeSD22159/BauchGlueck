package de.frederikkohler.bauchglueck.ui.screens.authScreens.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.profileSlider.ProfileSlider
import de.frederikkohler.bauchglueck.ui.components.profileSlider.ProfileSliderUnit
import de.frederikkohler.bauchglueck.ui.navigations.Destination
import org.lighthousegames.logging.logging
import viewModel.FirebaseAuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen(
    navController: NavController,
    firebaseAuthViewModel: FirebaseAuthViewModel,
) {
    val currentProfile by firebaseAuthViewModel.userFormState.value.userProfile.collectAsState()

    LaunchedEffect(currentProfile) {
        logging().info { "currentProfile: ${currentProfile.toString()}" }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        currentProfile.let { profile ->

            InfoCard(profile.firstName, profile.surgeryDateTimeStamp)

            SurgeryDatePicker(
                firebaseAuthViewModel
            ) {
                logging().info { "SurgeryDatePicker: $it" }
                firebaseAuthViewModel.onUpdateUserProfile(profile.copy(surgeryDateTimeStamp = it))
            }

            ProfileSlider(
                label = stringResource(R.string.settings_sheet_startgewicht_label),
                value = profile.startWeight,
                onValueChange = {
                    firebaseAuthViewModel.onUpdateUserProfile(profile.copy(startWeight = it))
                },
                valueRange = 40f..300f,
                steps = 260,
                unit = stringResource(R.string.settings_sheet_startgewicht_unit),
                unitType = ProfileSliderUnit.Int
            )

            ProfileSlider(
                label = stringResource(R.string.settings_sheet_wasseraufnahme_label),
                value = profile.waterIntake,
                onValueChange = {
                    firebaseAuthViewModel.onUpdateUserProfile(profile.copy(waterIntake = it))
                },
                valueRange = 20f..500f,
                steps = 480,
                unit = stringResource(R.string.settings_sheet_wasseraufnahme_unit),
                unitType = ProfileSliderUnit.Int
            )

            ProfileSlider(
                label = stringResource(R.string.settings_sheet_wasseraufnahme_am_tag_label),
                value = profile.waterDayIntake,
                onValueChange = {
                    logging().info { "onValueChange: $it" }
                    firebaseAuthViewModel.onUpdateUserProfile(profile.copy(waterDayIntake = it))
                },
                valueRange = 1.0f..3.5f,
                steps = 25,
                unit = stringResource(R.string.settings_sheet_wasseraufnahme_am_tag_unit),
                unitType = ProfileSliderUnit.Double
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (profile.betweenMeals > 3) {
                        firebaseAuthViewModel.onUpdateUserProfile(profile.copy(betweenMeals = profile.betweenMeals - 1))
                    }
                }) {
                    Text("-")
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${profile.betweenMeals}")
                    Text("Zwischenmahlzeiten")
                }

                Button(onClick = {
                    if (profile.betweenMeals < 6) {
                        firebaseAuthViewModel.onUpdateUserProfile(profile.copy(betweenMeals = profile.betweenMeals + 1))
                    }
                }) {
                    Text("+")
                }
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (profile.mainMeals > 3) {
                        firebaseAuthViewModel.onUpdateUserProfile(profile.copy(mainMeals = profile.mainMeals - 1))
                    }
                }) {
                    Text("-")
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${profile.mainMeals}")
                    Text("Hauptmahlzeiten")
                }

                Button(onClick = {
                    if (profile.mainMeals < 6) {
                        firebaseAuthViewModel.onUpdateUserProfile(profile.copy(mainMeals = profile.mainMeals + 1))
                    }
                }) {
                    Text("+")
                }
            }


            SignOutContainer {
                firebaseAuthViewModel.onLogout()
                navController.navigate(Destination.Login.route)
            }
        }
    }
}
