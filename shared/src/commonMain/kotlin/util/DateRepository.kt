package util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.lighthousegames.logging.logging

object DateRepository {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val tomorrow = today.plus(1, DateTimeUnit.DAY)

    fun getNextSevenDays(): List<LocalDate> {
        return (0..7).map { today.plus(it, DateTimeUnit.DAY) }
    }

    var getTheNextMonthDays: List<LocalDate>
        get() {
            return (0..30).map { today.plus(it, DateTimeUnit.DAY) }
        }
        set(value) {}

    var getTheLastMonthDays: List<LocalDate>
        get() {
            return (0..30).map { today.minus(it, DateTimeUnit.DAY) }
        }
        set(value) {}

    fun getFirstAndLastDayOfSevenPreviousWeeks(): List<Pair<LocalDate, LocalDate>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val firstDayOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        val lastDayOfWeek = firstDayOfWeek.plus(6, DateTimeUnit.DAY)

        val weekDates: MutableList<Pair<LocalDate, LocalDate>> = mutableListOf()

        (7 downTo 0).map { weekOffset ->
            val startDate = firstDayOfWeek.minus(weekOffset, DateTimeUnit.WEEK)
            val endDate = lastDayOfWeek.minus(weekOffset, DateTimeUnit.WEEK)

            val pair = Pair(startDate, endDate)
            weekDates.add(pair)
        }

        return weekDates.toList()
    }

    fun getPreviousSevenDays(): List<LocalDate> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return (0..7).map { today.minus(it, DateTimeUnit.DAY) }
    }

    fun getCurrentDate(): LocalDate {
        return Clock.System.todayIn(TimeZone.currentSystemDefault())
    }

    fun startEndToday(): Today {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now().toLocalDateTime(timeZone)
        val todayStart = now.date.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val todayEnd = todayStart + 86_400_000

        return Today(todayStart, todayEnd)
    }

    val dayOfWeekName: String
        get() =  Weekday.entries[today.dayOfWeek.ordinal].displayName

    val dayOfWeek: Int
        get() = today.dayOfWeek.ordinal

    val todayDateString: String
        get() = today.dayOfMonth.toString().padStart(2,'0')
}

data class Today(
    val start: Long,
    val end: Long
)

val LocalDate.dayOfMonth: Int
    get() = this.dayOfMonth

fun LocalDate.toDateString(): String {
    val day= this.dayOfMonth.toStringAndPadStart(2, '0')
    val month= this.monthNumber.toStringAndPadStart(2, '0')
    val year = this.year.toString()
    return "${day}.${month}.${year}"
}

fun Int.toStringAndPadStart(length: Int, fillChar: Char): String {
    return this.toString().padStart(length, fillChar)
}
// -> 2024-09-19 12:22   -> 2024-09-19
fun Long.isTimestampOnDate(date: LocalDate, timeZone: TimeZone = TimeZone.UTC): Boolean {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateFromTimestamp = instant.toLocalDateTime(timeZone).date
    return localDateFromTimestamp == date
}

enum class Weekday(val displayName: String) {
    Montag("Montag"),
    Dienstag("Dienstag"),
    Mittwoch("Mittwoch"),
    Donnerstag("Donnerstag"),
    Freitag("Freitag"),
    Samstag("Samstag"),
    Sonntag("Sonntag");
}