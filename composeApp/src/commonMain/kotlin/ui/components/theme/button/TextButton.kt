package ui.components.theme.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.components.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.theme.AppTheme

@Composable
fun TextButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    shape: RoundedCornerShape = RoundedCornerShape(50.dp),
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
                    shape = shape
                )
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .clickableWithRipple {
                    onClick()
                },
        ) {
            BodyText(text = text, color = color)
        }
    }
}