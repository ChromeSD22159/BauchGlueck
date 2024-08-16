package de.frederikkohler.bauchglueck.ui.screens.authScreens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ScrollingView
import de.frederikkohler.bauchglueck.ui.components.HeadCard
import de.frederikkohler.bauchglueck.ui.theme.AppTheme

@Composable
fun HomeTimerCard(
    title: String = "Timer",
    onNavigate: () -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = title
        )

        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(2.dp))
            listOf(1,2,3,4,5).forEach { _ ->

                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                        .clickable {
                            onNavigate()
                        }
                ){
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "icon",
                        modifier = Modifier
                            .size(75.dp)
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeTimerCardPreview() {
    AppTheme {
        Column {
            Text(
                modifier = Modifier,
                style = MaterialTheme.typography.bodyMedium,
                text = "Timer"
            )

            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                listOf(1,2,3).forEach {
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .width(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(16.dp)

                    ){
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "icon",
                            modifier = Modifier
                                .size(75.dp)
                        )
                    }
                }
            }
        }
    }
}
