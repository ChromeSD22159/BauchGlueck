package ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.components.theme.clickableWithRipple
import ui.theme.AppTheme

@Composable
@Preview(
    showBackground = true
)
fun ClassPreview(){
    AppTheme {
        Box(
            Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            FillableGlassWithAnimation(MaterialTheme.colorScheme.surface)
        }
    }
}

@Composable
fun FillableGlassWithAnimation(
    bgColor: Color = MaterialTheme.colorScheme.surface,
    defaultSize: Dp = 100.dp,
    isFilled: Boolean = false,
    animationDelay: Long = 0L
) {

    val fillLevel = remember { Animatable(0.1f) }
    var animationState by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val startAnimation = {
        scope.launch {
            if (!animationState) {
                animationState = true
                fillLevel.animateTo(
                    targetValue = 0.8f,
                    animationSpec = tween(durationMillis = 1000)
                )
                animationState = false
            }
        }
    }

    LaunchedEffect(isFilled) {
        if (isFilled) {
            delay(animationDelay) // Verzögerung, bevor die Animation startet
            startAnimation() // Start der Animation
        }
    }

    Spacer(
        modifier = Modifier
            .size(defaultSize)
            .clickableWithRipple { startAnimation() }
            .background(color = Color.Cyan)
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
                        color = Color.Blue,
                        style = Fill
                    )
                }
            } // WASSER
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
                    drawPath(path, Color.Gray.copy(alpha = 0.3f), style = Stroke(width = percent / 10))
                }
            }
    )
}