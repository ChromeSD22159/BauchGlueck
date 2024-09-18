package ui.screens.authScreens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.icon_plus
import org.jetbrains.compose.resources.vectorResource
import org.lighthousegames.logging.logging
import ui.components.FillableGlassWithAnimation
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText
import viewModel.FirebaseAuthViewModel
import viewModel.WaterIntakeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WaterIntakeCard(
    modifier: Modifier = Modifier,
    firebaseAuthViewModel: FirebaseAuthViewModel
) {
    val glassSize = 0.25
    val waterIntakeViewModel: WaterIntakeViewModel = WaterIntakeViewModel()
    val intakesToday by waterIntakeViewModel.intakesToday.collectAsStateWithLifecycle(initialValue = emptyList())
    val totalIntakeInLiter = intakesToday.sumOf { it.value }
    val drunkenGlasses = (totalIntakeInLiter / glassSize).toInt()

    val target = firebaseAuthViewModel.userFormState.value.userProfile.value.waterDayIntake
    val glasses = (target / glassSize).toInt()

    Section(
        sectionModifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(MaterialTheme.shapes.large.topEnd))
        ) {
            Row(
                modifier = modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column {

                    HeadlineText(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        size = 16.sp,
                        weight = FontWeight.Medium,
                        text = "Wassereinnahme"
                    )

                    FooterText(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        size = 12.sp,
                        text = "Dein Ziel: ${target}L Wasser"
                    )

                    HeadlineText(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        size = 24.sp,
                        weight = FontWeight.Medium,
                        text = "%.2fL".format(totalIntakeInLiter)
                    )

                    // Glasses
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        repeat(glasses) { index ->
                            FillableGlassWithAnimation(
                                bgColor = MaterialTheme.colorScheme.surface,
                                defaultSize = 40.dp,
                                isFilled = index < drunkenGlasses,
                                animationDelay = index * 100L
                            )
                        }
                    }


                    // AddWater
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.clickableWithRipple { waterIntakeViewModel.insertIntake() },
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(15.dp),
                                imageVector = vectorResource(resource = Res.drawable.icon_plus),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onBackground
                            )

                            FooterText(text = "Wasser hinzufügen")
                        }
                    }
                }
            }
        }
    }
}