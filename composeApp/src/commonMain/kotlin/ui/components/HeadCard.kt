package ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource

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
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
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
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f))
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = vectorResource(icon),
                contentDescription = "icon",
                modifier = Modifier
                    .size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        CardTitle(title)

        Spacer(modifier = Modifier.weight(1f))

        if (arrowIcon) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Trending Up",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CardTitle(title: String) {
    Text(
        modifier = Modifier,
        style = MaterialTheme.typography.bodyMedium,
        text = title
    )
}