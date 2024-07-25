import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview

import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.compose_multiplatform
import de.frederikkohler.bauchglueck.ui.theme.BkTheme

@Composable
fun App() {
    BkTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showContent = !showContent },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                Text(
                    text = "Click me!",
                )
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}

@Composable
@Preview
fun AppButtonDarkPreview() {
    BkTheme(darkTheme = true) {
        App()
    }
}

@Composable
@Preview
fun AppButtonLightPreview() {
    BkTheme(darkTheme = false) {
        App()
    }
}