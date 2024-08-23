package de.frederikkohler.bauchglueck.ui.screens.authScreens.settingsSheet

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import de.frederikkohler.bauchglueck.viewModel.FirebaseAuthViewModel
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import data.FirebaseConnection
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.profileSlider.ProfileSlider
import de.frederikkohler.bauchglueck.ui.components.profileSlider.ProfileSliderUnit
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun SettingSheet(
    showSettingSheet: Boolean,
    icon: Int = R.drawable.icon_gear,
    onDismissRequest: () -> Unit,
    onSignOut: () -> Unit,
    firebaseAuthViewModel: FirebaseAuthViewModel,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = showSettingSheet) {
        if (showSettingSheet) {
            scope.launch {
                sheetState.show()
            }
        } else {
            scope.launch {
                sheetState.hide()
                firebaseAuthViewModel.saveUserProfile(FirebaseConnection.Remote)
            }
        }
    }

    if(showSettingSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    bottom = WindowInsets
                        .navigationBarsIgnoringVisibility
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            containerColor = MaterialTheme.colorScheme.background,
            sheetState = sheetState,
            scrimColor = Color.Transparent,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            dragHandle = { DragHandle(icon) },
            onDismissRequest = onDismissRequest,
        ) {
            SettingSheetContent(
                firebaseAuthViewModel = firebaseAuthViewModel,
                onSignOut = onSignOut
            )
        }
    }

}

@Composable
fun DragHandle(icon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = "icon",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            style = MaterialTheme.typography.titleSmall,
            text = stringResource(R.string.settings_sheet_title),
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingSheetContent(
    firebaseAuthViewModel: FirebaseAuthViewModel,
    onSignOut: () -> Unit
) {
    val user by firebaseAuthViewModel.userProfile.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        user?.let { userProfile ->
            InfoCard(userProfile)

            SurgeryDatePicker(firebaseAuthViewModel)

            ProfileSlider(
                label = stringResource(R.string.settings_sheet_startgewicht_label),
                value = userProfile.startWeight,
                onValueChange = {
                    val updatedProfile = userProfile.copy(startWeight = it)
                    firebaseAuthViewModel.updateUserProfile(updatedProfile)
                },
                valueRange = 40f..300f,
                steps = 260,
                unit = stringResource(R.string.settings_sheet_startgewicht_unit),
                unitType = ProfileSliderUnit.Int
            )

            ProfileSlider(
                label = stringResource(R.string.settings_sheet_wasseraufnahme_label),
                value = userProfile.waterIntake,
                onValueChange = {
                    val updatedProfile = userProfile.copy(waterIntake = it)
                    firebaseAuthViewModel.updateUserProfile(updatedProfile)
                },
                valueRange = 20f..500f,
                steps = 480,
                unit = stringResource(R.string.settings_sheet_wasseraufnahme_unit),
                unitType = ProfileSliderUnit.Int
            )

            ProfileSlider(
                label = stringResource(R.string.settings_sheet_wasseraufnahme_am_tag_label),
                value = userProfile.waterDayIntake / 1000,
                onValueChange = {
                    val updatedProfile = userProfile.copy(waterDayIntake = it * 1000)
                    firebaseAuthViewModel.updateUserProfile(updatedProfile)
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
