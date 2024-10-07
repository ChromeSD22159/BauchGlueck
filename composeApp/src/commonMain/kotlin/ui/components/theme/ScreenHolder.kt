package ui.components.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_arrow_left
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.text.HeadlineText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenHolder(
    title: String = "Titel",
    showBackButton: Boolean = false,
    onNavigate: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    foreground: Color = MaterialTheme.colorScheme.onBackground,
    pageSpacing: Dp = 10.dp,
    itemSpacing: Dp = 10.dp,
    optionsRow: @Composable (Color) -> Unit = { },
    contentColumn: @Composable () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(5.dp)
                    .background(backgroundColor)
                    .padding(top = 30.dp, bottom = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier.weight(3f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (showBackButton) {
                            Icon(
                                modifier = Modifier.clickableWithRipple { onNavigate() },
                                imageVector = vectorResource(resource = Res.drawable.ic_arrow_left),
                                contentDescription = "${title}.image",
                                tint = foreground
                            )
                        }

                        HeadlineText(
                            size = if(showBackButton) MaterialTheme.typography.bodyMedium.fontSize else MaterialTheme.typography.titleMedium.fontSize,
                            text = title,
                            color = foreground
                        )
                    }

                    Row(
                        Modifier.weight(2f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        optionsRow(foreground)
                    }
                }
            }
        }
        item {
            Box(
                modifier = Modifier.padding(pageSpacing)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    contentColumn()

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}