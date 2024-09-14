package ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import ui.theme.AppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_gear
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RoundImageButton(
    icon: DrawableResource,
    modifier: Modifier = Modifier,
    action: () -> Unit = {}
) {
    Icon(
        imageVector =  vectorResource(icon),
        contentDescription = "icon",
        tint = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary, // Start color
                        MaterialTheme.colorScheme.onPrimary // End color
                    )
                )
            )
            .padding(5.dp)
            .clickable {
                action()
            },
    )
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun RoundImageButtonPreview() {
    AppTheme {
        RoundImageButton(
            icon = Res.drawable.ic_gear,
            modifier = Modifier
        )
    }
}