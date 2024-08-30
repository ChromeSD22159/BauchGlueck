package de.frederikkohler.bauchglueck.ui.screens.authScreens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.frederikkohler.bauchglueck.R
import de.frederikkohler.bauchglueck.ui.components.HeadCard
import de.frederikkohler.bauchglueck.ui.theme.AppTheme

@Composable
fun HomeMedicationCard(
    title: String = "Medikation",
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
fun HomeMedicationCardPreview() {
    AppTheme {
        HomeWaterIntakeCard(onNavigate = {})
    }
}
