package ui.components.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.extentions.backgroundVerticalGradient
import ui.components.extentions.sectionShadow
import ui.components.theme.text.BodyText

@Composable
fun HeadCard(
    modifier: Modifier = Modifier,
    icon: DrawableResource? = null,
    title: String,
    arrowIcon: Boolean = true,
    onNavigate: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onNavigate() }
            .fillMaxWidth()
            .sectionShadow(),
        verticalArrangement = Arrangement.spacedBy(space = 0.dp, alignment = Alignment.CenterVertically)
    ) {
        icon?.let {
            CardHeadRow(it, title, arrowIcon = arrowIcon)
        }

        content()
    }
}

@Composable
fun CardHeadRow(icon: DrawableResource, title: String, arrowIcon: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .backgroundVerticalGradient()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = vectorResource(icon),
                contentDescription = "icon",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        CardTitle(title)

        Spacer(modifier = Modifier.weight(1f))

        if (arrowIcon) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Trending Up",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun CardTitle(title: String) {
    BodyText(
        text = title,
        color = MaterialTheme.colorScheme.onPrimary
    )
}