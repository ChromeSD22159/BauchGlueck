package ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.rodetta
import org.jetbrains.compose.resources.Font
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font

@Composable
fun themeTypography() = Typography().run {
    val myFontFamily = Font(
        Res.font.rodetta,
        weight = FontWeight.Normal
    ).toFontFamily()

    val source =  Font(
        googleFont = GoogleFont("Source Sans 3"), // Aclonica
        fontProvider = provider,
    ).toFontFamily()

    copy(
        displayLarge = displayLarge.copy(fontFamily = myFontFamily),
        displayMedium = displayMedium.copy(fontFamily = myFontFamily),
        displaySmall = displaySmall.copy(fontFamily = myFontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = myFontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = myFontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = myFontFamily),
        titleLarge = titleLarge.copy(fontFamily = myFontFamily),
        titleMedium = titleMedium.copy(fontFamily = myFontFamily),
        titleSmall = titleSmall.copy(fontFamily = myFontFamily),
        bodyLarge = bodyLarge.copy(fontFamily =  source),
        bodyMedium = bodyMedium.copy(fontFamily = source),
        bodySmall = bodySmall.copy(fontFamily = source),
        labelLarge = labelLarge.copy(fontFamily = source),
        labelMedium = labelMedium.copy(fontFamily = source),
        labelSmall = labelSmall.copy(fontFamily = source)
    )
}