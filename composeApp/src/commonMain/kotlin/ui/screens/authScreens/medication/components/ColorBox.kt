package ui.screens.authScreens.medication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ColorBox(
    height: Dp = 16.dp,
    cornerRadius: Dp = 6.dp,
    percentage: Float,
    isInFuture: Boolean = false,
    isToday: Boolean = false
) {
    val fillColor = if (isInFuture) Color.Transparent
            else if (percentage == 0.0f) Color.Gray.copy(0.1f)
            else MaterialTheme.colorScheme.primary.copy(alpha = percentage)

    val borderColorShown = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
        )
    )

    val borderColorHidden = Brush.verticalGradient(listOf(Color.Transparent,Color.Transparent))

    Box(
        modifier = Modifier
            .size(height)
            .background(
                color = fillColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = 0.5.dp,
                brush = if (isToday) borderColorShown else borderColorHidden,
                shape = RoundedCornerShape(cornerRadius)
            ),
    )
}