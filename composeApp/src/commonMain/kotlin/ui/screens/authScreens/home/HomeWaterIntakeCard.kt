package ui.screens.authScreens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.R
import ui.components.HeadCard
import ui.theme.AppTheme

@Composable
fun HomeWaterIntakeCard(
    title: String = "Wasseraufnahme",
    onNavigate: () -> Unit
) {
    HeadCard(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        title = title,
        icon = R.drawable.ic_water_drop,
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
fun HomeWaterIntakeCardPreview() {
    AppTheme {
        HomeWaterIntakeCard(onNavigate = {})
    }
}