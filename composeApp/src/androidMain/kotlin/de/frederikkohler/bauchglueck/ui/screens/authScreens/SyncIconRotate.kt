package de.frederikkohler.bauchglueck.ui.screens.authScreens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.R

@Composable
fun SyncIconRotate() {
    Box(modifier = Modifier.padding(end = 16.dp)) {
        var angle by remember { mutableFloatStateOf(0f) }
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = ""
        )

        angle = rotationAngle

        Box(
            modifier = Modifier
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
        ) {
            Icon(
                imageVector =  ImageVector.vectorResource(id = R.drawable.icon_sync),
                contentDescription = "icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(30.dp)
                    .rotate(angle)
            )
        }
    }
}