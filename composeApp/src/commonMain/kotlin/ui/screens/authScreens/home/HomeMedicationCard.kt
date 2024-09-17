package ui.screens.authScreens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_water_drop
import ui.components.theme.HeadCard

@Composable
fun HomeMedicationCard(
    title: String = "Medikation",
    horizontalSpacing: Dp = 10.dp,
    onNavigate: () -> Unit
) {
    HeadCard(
        modifier = Modifier.padding(horizontal = horizontalSpacing),
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
