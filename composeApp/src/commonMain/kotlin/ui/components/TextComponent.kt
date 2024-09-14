package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import ui.theme.AppTheme

@Composable
fun TextElement(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int? = null,
    component: TextComponent = TextComponent.ContinuousText,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit? = TextComponent.ContinuousText.fontSize,
    lineHeight: Float? = TextComponent.ContinuousText.lineHeight
){
    Text(
        modifier = modifier,
        fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
        fontWeight = fontWeight ?: component.fontWeight,
        fontSize = fontSize ?: component.fontSize,
        lineHeight = (fontSize ?: component.fontSize) * (lineHeight ?: component.lineHeight),
        maxLines = maxLines ?: Int.MAX_VALUE,
        overflow = TextOverflow.Ellipsis,
        text = text
    )
}

enum class TextComponent(val fontSize: TextUnit, val fontWeight: FontWeight = FontWeight.Normal, val lineHeight: Float) {
    HeadLine(16.sp, FontWeight.Bold, 1.25f),
    SubLine(14.sp, FontWeight.Bold, 1.25f),
    ContinuousText(12.sp, FontWeight.Normal, 1.5f),
    Small(10.sp, FontWeight.Thin, 1.25f)
}

@Preview(showBackground = true)
@Composable
fun TextPreview() {
    AppTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextElement(
                text="The HeadLine",
                component = TextComponent.HeadLine,
                fontSize = 14.sp
            )
            TextElement(
                text = "The Subline",
                component = TextComponent.SubLine,
                fontSize = 12.sp
            )
            TextElement(
                text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                component = TextComponent.ContinuousText,
                maxLines = 6,
                fontSize = 10.sp
            )
            TextElement(
                text = "Small Text",
                component = TextComponent.Small
            )
        }
    }
}