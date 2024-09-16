package ui.components.theme.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_arrow_right
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.clickableWithRipple
import ui.theme.AppTheme

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    resource: DrawableResource = Res.drawable.ic_arrow_right,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit = {}
) {
    AppTheme {
        Box(
            modifier = modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .clickableWithRipple {
                    onClick()
                },
        ) {
            Icon(
                imageVector = vectorResource(resource),
                contentDescription = "",
                tint = tint
            )
        }
    }
}