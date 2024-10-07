package ui.components.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.components.extentions.backgroundVerticalGradient
import ui.components.extentions.sectionShadow
import ui.navigations.Destination

@Composable
fun SliderItemAddCard(
    destination: Destination,
    onNavigate: (Destination) -> Unit
) {
    Box(
        modifier = Modifier
            .height(80.dp)
            .width(100.dp)
            .sectionShadow()
            .backgroundVerticalGradient()
            .clickableWithRipple {
                onNavigate(destination)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "icon",
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}