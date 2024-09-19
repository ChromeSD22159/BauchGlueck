package ui.screens.authScreens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_seal_xmark
import data.Repository
import data.local.entitiy.Node
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.vectorResource
import org.koin.java.KoinJavaComponent.inject
import ui.components.theme.SliderItemAddCard
import ui.components.theme.clickableWithRipple
import ui.components.theme.sectionShadow
import ui.components.theme.text.FooterText
import ui.components.theme.truncate
import ui.navigations.Destination
import util.DateRepository
import util.isTimestampOnDate

@Composable
fun LastNotesCalendar(
    title: String = "Medikation",
    horizontalSpacing: Dp = 10.dp,
    onNavigate: (Destination) -> Unit
) {
    val repository: Repository by inject(Repository::class.java)
    val nodeList by repository.nodeRepository.getAllNodes().collectAsState(initial = emptyList())

    val dates = DateRepository.getTheLastMonthDays
    val height = 80.dp

    LazyHorizontalGrid(
        modifier = Modifier.height(height + 10.dp),
        rows = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(horizontalSpacing),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(dates.size, key = { it }) { it ->
            val date = dates[it]
            val notes = nodeList.firstOrNull {
                it.date.isTimestampOnDate(date)
            }

            CalendarItem(date, height, notes) {
                onNavigate(Destination.AddNote)
            }
        }

        item {
            SliderItemAddCard(Destination.AddNote) {
                onNavigate(it)
            }
        }
    }
}


@Composable
fun CalendarItem(
    localDate: LocalDate,
    height: Dp,
    node: Node? = null,
    onNavigate: (Destination) -> Unit
) {

    val day = localDate.dayOfMonth.toString().padStart(2, '0')
    val month = localDate.monthNumber.toString().padStart(2, '0')

    Box(
        modifier = Modifier
            .height(height)
            .width(100.dp)
            .sectionShadow()
            .clickableWithRipple {
                if(node != null) {
                    onNavigate(Destination.AddNote)
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            FooterText(
                text = "${day}.${month}",
                size = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            if(node != null) {
                FooterText(
                    text = node.text.truncate(10),
                    size = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Icon(
                    imageVector = vectorResource(resource = Res.drawable.ic_seal_xmark),
                    contentDescription = "icon",
                    modifier = Modifier.size(75.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

        }
    }
}