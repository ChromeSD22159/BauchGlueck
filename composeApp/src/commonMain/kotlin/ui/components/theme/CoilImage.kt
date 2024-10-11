package ui.components.theme

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.placeholder_image
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource

@Composable
fun CoilImage(
    modifier: Modifier = Modifier,
    url: String,
    contentScale: ContentScale = ContentScale.FillHeight,
) {
    AsyncImage(
        model = coilImageRequest(url),
        placeholder = painterResource(Res.drawable.placeholder_image),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier.fillMaxHeight()
    )
}