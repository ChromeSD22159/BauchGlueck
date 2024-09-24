package ui.components.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.backgroundVerticalGradient(): Modifier {
    return this.background(
        brush = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    )
}

@Composable
fun Modifier.sectionShadow(color: Color? = null): Modifier {
    return this.shadow(
        elevation = 6.dp,
        shape = RoundedCornerShape(8.dp)
    )
    .background(
        color = color ?: MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    )
}

fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    return if (this.length > maxLength) {
        this.take(maxLength) + suffix
    } else {
        this
    }
}

fun Double.decimal(int: Int = 1): String {
    return "%.${int}f".format(this)
}