package ui.components.profileSlider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.get

@Composable
fun ProfileSlider(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    unit: String,
    unitType: ProfileSliderUnit = ProfileSliderUnit.Int
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
            interactionSource = remember { MutableInteractionSource() }
        )

        val formattedValue = when (unitType) {
            ProfileSliderUnit.Int -> value.toInt().toString()
            ProfileSliderUnit.Double -> String.format("%.1f", value)
            ProfileSliderUnit.Float -> value.toString()
        }

        Text("$label: $formattedValue $unit")
    }
}