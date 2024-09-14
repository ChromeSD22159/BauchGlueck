package ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.Rodetta
import org.jetbrains.compose.resources.Font
import androidx.compose.material3.Typography
import androidx.compose.ui.text.googlefonts.GoogleFont

@Composable
fun rodettaFontFamily() = FontFamily(
    Font(Res.font.Rodetta, weight = FontWeight.Light),
    Font(Res.font.Rodetta, weight = FontWeight.Normal),
    Font(Res.font.Rodetta, weight = FontWeight.Medium),
    Font(Res.font.Rodetta, weight = FontWeight.SemiBold),
    Font(Res.font.Rodetta, weight = FontWeight.Bold)
)

val displayFontFamilyTest = FontFamily(
    androidx.compose.ui.text.googlefonts.Font(
        googleFont = GoogleFont("Fredoka"), // Aclonica
        fontProvider = provider,
    )
)

@Composable
fun myTypography() = Typography().run {

    val fontFamily = rodettaFontFamily()
    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  displayFontFamilyTest),
        bodyMedium = bodyMedium.copy(fontFamily = displayFontFamilyTest),
        bodySmall = bodySmall.copy(fontFamily = displayFontFamilyTest),
        labelLarge = labelLarge.copy(fontFamily = displayFontFamilyTest),
        labelMedium = labelMedium.copy(fontFamily = displayFontFamilyTest),
        labelSmall = labelSmall.copy(fontFamily = displayFontFamilyTest)
    )
}