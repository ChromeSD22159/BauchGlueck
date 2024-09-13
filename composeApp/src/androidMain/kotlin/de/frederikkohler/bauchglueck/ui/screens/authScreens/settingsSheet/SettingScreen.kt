package de.frederikkohler.bauchglueck.ui.screens.authScreens.settingsSheet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.profileSlider.ProfileSlider
import de.frederikkohler.bauchglueck.ui.components.profileSlider.ProfileSliderUnit
import org.lighthousegames.logging.logging
import viewModel.FirebaseAuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingScreen(
    firebaseAuthViewModel: FirebaseAuthViewModel,
    onSignOut: () -> Unit
) {
    val currentProfile by firebaseAuthViewModel.userFormState.collectAsState()

    LaunchedEffect(currentProfile) {
        logging().info { "currentProfile: ${currentProfile.toString()}" }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        currentProfile.userProfile?.let { profile ->

            InfoCard(profile.firstName, profile.surgeryDateTimeStamp)

            SurgeryDatePicker(firebaseAuthViewModel)

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
                value = profile.waterDayIntake / 1000,
                onValueChange = {
                    firebaseAuthViewModel.onUpdateUserProfile(profile.copy(waterDayIntake = it))
                },
                valueRange = 1.0f..3.5f,
                steps = 25,
                unit = stringResource(R.string.settings_sheet_wasseraufnahme_am_tag_unit),
                unitType = ProfileSliderUnit.Double
            )

            SignOutContainer(
                onSignOut = onSignOut,
            )
        }
    }
}
