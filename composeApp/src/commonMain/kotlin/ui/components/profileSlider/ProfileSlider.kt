package ui.components.profileSlider

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            modifier = Modifier.fillMaxWidth()
        )

        val formattedValue = when (unitType) {
            ProfileSliderUnit.Int -> value.toInt().toString()
            ProfileSliderUnit.Double -> String.format("%.1f", value)
            ProfileSliderUnit.Float -> value.toString()
        }

        Text("$label: $formattedValue $unit")
    }
}