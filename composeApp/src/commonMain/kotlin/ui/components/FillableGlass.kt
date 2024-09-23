package ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_plus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.clickableWithRipple
import kotlin.random.Random

@Composable
fun FillableGlassWithAnimation(
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.surface,
    defaultSize: Dp = 100.dp,
    isFilled: Boolean = false,
    isActive: Boolean = false,
    onClick: () -> Unit,
    animationDelay: Long = 0L
) {
    var bubbles by remember { mutableStateOf(emptyList<Bubble>()) }
    val fillLevel = remember { Animatable(0.1f) }
    var animationState by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val startAnimation = {
        scope.launch {
            if (!animationState) {
                animationState = true
                delay(200)
                fillLevel.animateTo(
                    targetValue = 0.8f,
                    animationSpec = tween(durationMillis = 800)
                )
                animationState = false
            }
        }
    }

    val startAndBackAnimation = {
        scope.launch {
            animationState = true
            while (animationState) {
                // Füllstand erhöhen
                fillLevel.animateTo(
                    targetValue = 0.8f,
                    animationSpec = tween(durationMillis = 800)
                )

                delay(100)

                // Füllstand verringern
                fillLevel.animateTo(
                    targetValue = 0.2f,
                    animationSpec = tween(durationMillis = 200)
                )

                // Kurze Pause
                delay(500)
                animationState = false
            }
        }
    }

    LaunchedEffect(isFilled) {
        if (isFilled) {
            delay(animationDelay) // Verzögerung, bevor die Animation startet
            bubbles = generateBubbles(100f, 100f, 10)
            startAnimation() // Start der Animation
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            bubbles = updateBubbles(16L, bubbles, 100f) // Update every 16ms (approx 60fps)
            delay(16L) // Control the update rate to approx 60fps
        }
    }

    Box() {
        Spacer(
            modifier = Modifier
                .size(defaultSize)
                .clickableWithRipple {
                    if (isActive) {
                        onClick()
                        startAnimation()
                    } else {
                        startAndBackAnimation()
                    }
                }
                .background(color = Color.Cyan.copy(alpha = 0.5f))
                .drawWithCache {
                    onDrawBehind {
                        // WASSER
                        val glassWidth = size.width
                        val glassHeight = size.height
                        val waterHeight = glassHeight * fillLevel.value

                        // Zeichne die Wellenlinie oben auf dem Wasser
                        val wavePath = Path().apply {
                            val waveHeight = 10f  // Höhe der Welle
                            val waveLength = glassWidth / 4f  // Länge der einzelnen Welle

                            // Startpunkt der Welle
                            moveTo(0f, glassHeight - waterHeight)

                            // Erstelle eine Reihe von Bezier-Kurven, um die Wellen zu simulieren
                            for (i in 0..3) {
                                val startX = i * waveLength
                                val endX = (i + 1) * waveLength
                                val midX = (startX + endX) / 2

                                cubicTo(
                                    midX,
                                    glassHeight - waterHeight - waveHeight,  // Kontrollpunkt 1 (oben)
                                    midX,
                                    glassHeight - waterHeight + waveHeight,  // Kontrollpunkt 2 (unten)
                                    endX,
                                    glassHeight - waterHeight  // Endpunkt
                                )
                            }

                            // Schließe den Pfad, um das Rechteck für das Wasser zu erzeugen
                            lineTo(glassWidth, glassHeight)
                            lineTo(0f, glassHeight)
                            close()
                        }

                        // Zeichne das Wasser mit Wellenlinie
                        drawPath(
                            path = wavePath,
                            color = Color(0xFF0EE6ED),
                            style = Fill
                        )
                    }
                } // WASSER
                .drawWithCache { // bubbles
                    onDrawBehind {
                        bubbles.forEach { bubble ->
                            drawCircle(
                                center = Offset(bubble.x, bubble.y),
                                radius = bubble.radius,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                } // BUBLLES
                .drawWithCache {
                    // Left Side Overlay
                    val width = size.width
                    val height = size.height
                    val percent = width / 5
                    val path = Path()
                    path.moveTo(0f, 0f)
                    path.lineTo(0f, height)
                    path.lineTo(percent, height)
                    path.close()
                    onDrawBehind {
                        drawPath(path, bgColor, style = Fill)
                    }
                } // LEFT
                .drawWithCache {
                    // Left Side Overlay
                    val width = size.width
                    val height = size.height
                    val percent = width / 5
                    val path = Path()
                    path.moveTo(width, 0f)
                    path.lineTo(width, height)
                    path.lineTo(width - percent, height)
                    path.close()
                    onDrawBehind {
                        drawPath(path, bgColor, style = Fill)
                    }
                } // RIGHT
                .drawWithCache {
                    val width = size.width
                    val height = size.height
                    val percent = width / 5
                    val path = Path()
                    path.moveTo(0f, 0f) // OL
                    path.lineTo(percent, height) // UL
                    path.lineTo(width - percent, height)
                    path.lineTo(width, 0f)
                    path.close()
                    onDrawBehind {
                        drawPath(
                            path,
                            Color.Gray.copy(alpha = 0.3f),
                            style = Stroke(width = percent / 10)
                        )
                    }
                } // Border
                .drawWithCache {
                    val width = size.width
                    val height = size.height
                    val percent = width / 5
                    val half = width / 2
                    val path = Path()
                    path.moveTo(0f, 0f) // OL
                    path.lineTo(percent, height) // UL
                    path.lineTo(width - percent, height)
                    path.lineTo(width, 0f)
                    path.close()
                    path.close()

                    val brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(0.15f),
                            Color.White.copy(0.2f),
                            Color.White.copy(0.3f),
                            Color.White.copy(0.15f),
                            Color.White.copy(0.15f)
                        )
                    )

                    onDrawBehind {
                        drawPath(path, brush, style = Fill)
                    }
                } // Schatten
        )

        if(isActive && !isFilled) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Center),
                imageVector = vectorResource(resource = Res.drawable.ic_plus),
                tint = Color.Black,
                contentDescription = "",
            )
        }
    }
}



fun updateBubbles(deltaTime: Long, bubbles: List<Bubble>, glassHeight: Float): List<Bubble> {
    val bubbleSpeed = 20f // Geschwindigkeit, mit der sich die Blasen nach oben bewegen (fester Wert)
    val updatedBubbles = bubbles.map { bubble ->
        var newY = bubble.y - deltaTime.toFloat() / 1000f * bubbleSpeed // Bewegt die Blasen nach oben

        // Überprüfe, ob die Blase das obere Ende erreicht hat und setze sie zurück
        if (newY + bubble.radius < 0) {
            newY = glassHeight // Setze die Blase nach unten zurück
        }

        // Gib die aktualisierte Blase zurück
        bubble.copy(y = newY)
    }

    return updatedBubbles
}

fun generateBubbles(glassWidth: Float, glassHeight: Float, numBubbles: Int): List<Bubble> {
    val newBubbles = mutableListOf<Bubble>()
    for (i in 0 until numBubbles) {
        val x = Random.nextFloat() * (glassWidth - 20f) + 10f // Vermeide Kollision mit dem Rand
        val y = Random.nextFloat() * (glassHeight - 40f) + 20f // Vermeide Kollision oben und unten
        val radius = Random.nextFloat() * 5f + 2f // Zufällige Größe der Blasen
        newBubbles.add(Bubble(x, y, radius))
    }
    return newBubbles
}


data class Bubble(
    var x: Float, // X-coordinate of the bubble center
    var y: Float, // Y-coordinate of the bubble center
    val radius: Float, // Radius of the bubble
)