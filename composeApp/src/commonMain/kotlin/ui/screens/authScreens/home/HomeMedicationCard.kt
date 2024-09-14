package ui.screens.authScreens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_water_drop
import ui.components.HeadCard
import ui.theme.AppTheme

@Composable
fun HomeMedicationCard(
    title: String = "Medikation",
    onNavigate: () -> Unit
) {
    HeadCard(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        title = title,
        icon = Res.drawable.ic_water_drop,
        onNavigate = { onNavigate() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ROW CONTENT
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeMedicationCardPreview() {
    AppTheme {
        HomeWaterIntakeCard(onNavigate = {})
    }
}
