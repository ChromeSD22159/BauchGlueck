package ui.components.theme.background

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.bubble_right_dark
import bauchglueck.composeapp.generated.resources.bubble_right_light
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview(showBackground = true)
fun AppBackgroundWithImage(
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(if (isDarkTheme) Res.drawable.bubble_right_dark else Res.drawable.bubble_right_light),
            contentDescription = "bubble_right_light",
            contentScale = ContentScale.FillWidth,
        )
    }
}