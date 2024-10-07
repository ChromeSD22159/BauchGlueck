package ui.screens.authScreens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_kochhut
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.text.BodyText
import ui.components.theme.text.HeadlineText

@Composable
fun SectionImageCard(
    image: DrawableResource = Res.drawable.ic_kochhut,
    title: String,
    description:String,
    scale: Float = 1.0f,
    offset: DpOffset,
    onNavigate: () -> Unit,
) {
    Section(
        sectionPadding = 0.dp,
        sectionModifier = Modifier
            .padding(horizontal = 10.dp)
            .clickableWithRipple {
                onNavigate()
            }
    ) {
        Box(
            modifier = Modifier
                .height(100.dp),
            contentAlignment = Alignment.CenterEnd
        ) {

            Image(
                modifier = Modifier
                    .offset(offset.x, offset.y)
                    .alpha(0.25f)
                    .size(200.dp)
                    .rotate(15f)
                    .scale(scale),
                imageVector = vectorResource(resource = image),
                contentScale = androidx.compose.ui.layout.ContentScale.FillHeight,
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeadlineText(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    size = 16.sp,
                    weight = FontWeight.Medium,
                    text = title
                )
                BodyText(
                    text = description,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}