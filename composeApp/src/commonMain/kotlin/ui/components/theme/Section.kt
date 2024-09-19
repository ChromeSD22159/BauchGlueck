package ui.components.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ui.components.theme.text.FooterText
import ui.theme.AppTheme

@Composable
fun Section(
    title: String? = null,
    sectionModifier: Modifier = Modifier,
    sectionShadowSize: Dp = 6.dp,
    sectionPadding: Dp = 12.dp,
    contentModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = sectionModifier
    ) {
        if(title != null) {
            Row(
                modifier = Modifier.padding(top = 10.dp, start = 12.dp),
            ) {
                FooterText(text = title.uppercase())
            }
        }
        Row(
            modifier = contentModifier
                .fillMaxWidth()
                .sectionShadow()
                .padding(sectionPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            content()
        }
    }
}