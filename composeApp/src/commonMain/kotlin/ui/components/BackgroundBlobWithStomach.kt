package ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.background_splash_big
import bauchglueck.composeapp.generated.resources.background_splash_small
import bauchglueck.composeapp.generated.resources.magen
import de.frederikkohler.bauchglueck.R

@Composable
fun BackgroundBlobWithStomach() {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.TopEnd
    ) {

        Image(
            painter = painterResource(Res.drawable.magen),
            contentDescription = "Stromach",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 25.dp, end = 10.dp)
                .width(150.dp)
                .height(150.dp)
        )

        Image(
            painter = painterResource(Res.drawable.background_splash_small),
            contentDescription = "image description",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(197.dp)
                .height(186.dp)
        )

        Image(
            painter = painterResource(Res.drawable.background_splash_big),
            contentDescription = "image description",
            contentScale = ContentScale.None,
            modifier = Modifier
                .padding(1.dp)
                .width(266.15442.dp)
                .height(283.81583.dp)
        )
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun BackgroundBlobWithStomachDarkPreview() {
    BackgroundBlobWithStomach()
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
fun BackgroundBlobWithStomachLightPreview() {
    BackgroundBlobWithStomach()
}