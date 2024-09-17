package ui.components.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.components.theme.text.FooterText
import ui.components.theme.text.HeadlineText

@Composable
fun IconCard(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 10.dp,
    iconLeft: String = "\uD83E\uDD2A",
    iconRight: String = "\uD83D\uDC7B",
    headline: String  = "Wie war dein Tag?",
    description: String = "Erfasse Notizen, Gefühle\noder Gedanken.",
    height: Dp = 100.dp,
    content: @Composable () -> Unit = {}
) {
    Section(
        sectionModifier = modifier.padding(horizontal = horizontalSpacing),
        sectionPadding = 0.dp
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(MaterialTheme.shapes.large.topEnd))
        ) {
            // LEFT
            Column(
                modifier = modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .height(height),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = (-10).dp),
                    textAlign = TextAlign.Start,
                    text = iconLeft,
                    fontSize = 50.sp
                )
            }

            // Right
            Column(
                modifier = modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .height(height),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = 10.dp),
                    textAlign = TextAlign.End,
                    text = iconRight,
                    fontSize = 50.sp
                )
            }

            // Center
            Row(
                modifier = modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
                    .height(height),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(

                ) {

                    HeadlineText(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        size = 16.sp,
                        weight = FontWeight.Medium,
                        text = headline
                    )

                    FooterText(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        size = 12.sp,
                        text = description
                    )

                    content()
                }
            }
        }
    }
}