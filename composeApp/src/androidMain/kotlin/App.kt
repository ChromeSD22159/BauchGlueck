import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.frederikkohler.bauchglueck.ui.theme.AppTheme
import de.frederikkohler.bauchglueck.ui.components.BackgroundBlobWithStomach

@Composable
fun App() {
    AppTheme {
        Box(
            modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopEnd
        ) {
            BackgroundBlobWithStomach()
        }
    }
}



@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun AppButtonDarkPreview() {
    AppTheme(darkTheme = true) {
        App()
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
fun AppButtonLightPreview() {
    AppTheme(darkTheme = false) {
        App()
    }
}