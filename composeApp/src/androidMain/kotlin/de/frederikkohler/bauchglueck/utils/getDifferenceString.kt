package de.frederikkohler.bauchglueck.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import de.frederikkohler.bauchglueck.R
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun getDifferenceDateString(timestamp: Long): String {
    val context = LocalContext.current
    val startDateTime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault())
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val isFuture = startDateTime > currentDateTime

    val (earlierDateTime, laterDateTime) = if (isFuture) {
        currentDateTime to startDateTime
    } else {
        startDateTime to currentDateTime
    }

    var years = laterDateTime.year - earlierDateTime.year
    var months = laterDateTime.monthNumber - earlierDateTime.monthNumber
    var days = laterDateTime.dayOfMonth - earlierDateTime.dayOfMonth

    if (days < 0) {
        val previousMonthDate = laterDateTime.date.minus(DatePeriod(months = 1))
        days += previousMonthDate.daysUntil(laterDateTime.date)
        months -= 1
    }

    if (months < 0) {
        months += 12
        years -= 1
    }

    val counterText = when {
        years > 0 -> context.getString(R.string.time_years_months_days, years, months, days)
        months > 0 -> context.getString(R.string.time_months_days, months, days)
        else -> context.getString(R.string.time_days, days)
    }

    return if (isFuture) {
        context.getString(R.string.time_until_restart, counterText)
    } else {
        context.getString(R.string.time_since_restart, counterText)
    }
}

fun getDifferenceDates(startTimestamp: Long, endTimestamp: Long): Triple<Int, Int, Int> {
    val startDateTime = Instant.fromEpochMilliseconds(startTimestamp).toLocalDateTime(TimeZone.currentSystemDefault())
    val endDateTime = Instant.fromEpochMilliseconds(endTimestamp).toLocalDateTime(TimeZone.currentSystemDefault())

    var years = endDateTime.year - startDateTime.year
    var months = endDateTime.monthNumber - startDateTime.monthNumber
    var days = endDateTime.dayOfMonth - startDateTime.dayOfMonth

    if (days < 0) {
        val previousMonthDate = endDateTime.date.minus(DatePeriod(months = 1))
        days += previousMonthDate.daysUntil(endDateTime.date)
        months -= 1
    }

    if (months < 0) {
        months += 12
        years -= 1
    }

    return Triple(years, months, days)
}