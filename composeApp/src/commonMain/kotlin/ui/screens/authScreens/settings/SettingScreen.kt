package ui.screens.authScreens.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_globe
import bauchglueck.composeapp.generated.resources.ic_grid_2_2
import bauchglueck.composeapp.generated.resources.ic_info
import bauchglueck.composeapp.generated.resources.ic_mail
import bauchglueck.composeapp.generated.resources.ic_star
import data.model.firebase.UserRole
import de.frederikkohler.bauchglueck.R
import ui.components.profileSlider.ProfileSlider
import ui.components.profileSlider.ProfileSliderUnit
import ui.navigations.Destination
import org.lighthousegames.logging.logging
import ui.components.FormScreens.PlusMinusButtonForms
import ui.components.theme.SectionLink
import ui.components.theme.Section
import ui.components.theme.ScreenHolder
import ui.components.theme.button.TextButton
import util.appVersion
import viewModel.FirebaseAuthViewModel


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.settingsComposable(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.Settings.route) {
        val currentProfile by firebaseAuthViewModel.userFormState.value.userProfile.collectAsState()

        LaunchedEffect(currentProfile) {
            logging().info { "currentProfile: ${currentProfile.toString()}" }
        }

        ScreenHolder(
            title = Destination.Settings.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.Home.route)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                currentProfile.let { profile ->

                    if(profile.role == UserRole.admin) {
                        TextButton(text = "Zum Admin Panel") {
                            navController.navigate(Destination.AdminPanel.route)
                        }
                    }

                    InfoCard(profile.firstName, profile.surgeryDateTimeStamp)

                    SurgeryDatePicker(
                        firebaseAuthViewModel
                    ) {
                        logging().info { "SurgeryDatePicker: $it" }
                        firebaseAuthViewModel.onUpdateUserProfile(profile.copy(surgeryDateTimeStamp = it))
                    }

                    Section {
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
                    }

                    /*
                    Section {
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
                    }
                     */

                    Section {
                        ProfileSlider(
                            label = stringResource(R.string.settings_sheet_wasseraufnahme_am_tag_label),
                            value = profile.waterDayIntake,
                            onValueChange = {
                                logging().info { "onValueChange: $it" }
                                firebaseAuthViewModel.onUpdateUserProfile(profile.copy(waterDayIntake = it))
                            },
                            valueRange = 1.5f..3.5f,
                            steps = 7,
                            unit = stringResource(R.string.settings_sheet_wasseraufnahme_am_tag_unit),
                            unitType = ProfileSliderUnit.Double
                        )
                    }

                    PlusMinusButtonForms(
                        title = "Hauptmahlzeiten",
                        displayColumn = {
                            Text("${profile.mainMeals}")
                        },
                        onMinus = {
                            if (profile.mainMeals > 3) {
                                firebaseAuthViewModel.onUpdateUserProfile(profile.copy(mainMeals = profile.mainMeals - 1))
                            }
                        },
                        onPlus = {
                            if (profile.mainMeals < 6) {
                                firebaseAuthViewModel.onUpdateUserProfile(profile.copy(mainMeals = profile.mainMeals + 1))
                            }
                        }
                    )

                    PlusMinusButtonForms(
                        title = "Zwischenmahlzeiten",
                        displayColumn = {
                            Text("${profile.betweenMeals}")
                        },
                        onMinus = {
                            if (profile.betweenMeals > 3) {
                                firebaseAuthViewModel.onUpdateUserProfile(profile.copy(betweenMeals = profile.betweenMeals - 1))
                            }
                        },
                        onPlus = {
                            if (profile.betweenMeals < 6) {
                                firebaseAuthViewModel.onUpdateUserProfile(profile.copy(betweenMeals = profile.betweenMeals + 1))
                            }
                        }
                    )

                    Section(
                        title = "Support"
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SectionLink(
                                icon = Res.drawable.ic_mail,
                                displayText = "Unterstützung + Feedback",
                                url = "https://www.instagram.com/frederik.kohler/"
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.25f)
                            )

                            SectionLink(
                                icon = Res.drawable.ic_star,
                                displayText = "Beweisete die App im Playstore",
                                url = "https://play.google.com/store/apps/details?id=de.frederikkohler.bauchglueck"
                            )
                        }
                    }

                    Section(
                        title = "Über den Entwickler"
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SectionLink(icon = Res.drawable.ic_globe, displayText = "Instagram des Entwicklers", url = "https://www.instagram.com/frederik.kohler/")

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.25f)
                            )

                            SectionLink(icon = Res.drawable.ic_globe, displayText = "Webseite des Entwicklers", url = "https://www.frederikkohler.de/")

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.25f)
                            )

                            SectionLink(icon = Res.drawable.ic_grid_2_2, displayText = "Apps des Entwicklers", url = "https://play.google.com/store/apps/developer?id=Frederik+Kohler")

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.25f)
                            )

                            SectionLink(icon = Res.drawable.ic_info, displayText = "Version ($appVersion)")
                        }
                    }

                    SignOutContainer {
                        firebaseAuthViewModel.onLogout()
                        navController.navigate(Destination.Login.route)
                    }
                }
            }
        }
    }
}