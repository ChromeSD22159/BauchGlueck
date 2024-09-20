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
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

object DateRepository {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val tomorrow = today.plus(1, DateTimeUnit.DAY)

    val todayUTC = Clock.System.todayIn(TimeZone.UTC)
    val tomorrowUTC = todayUTC.plus(1, DateTimeUnit.DAY)

    fun getNextSevenDays(): List<LocalDate> {
        return (0..7).map { today.plus(it, DateTimeUnit.DAY) }
    }

    val getTheNextMonthDaysUTC: List<LocalDate>
        get() {
            return (0..30).map { todayUTC.plus(it, DateTimeUnit.DAY) }
        }

    val getTheLastMonthDaysUTC: List<LocalDate>
        get() {
            return (0..30).map { todayUTC.minus(it, DateTimeUnit.DAY) }
        }

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

    fun startEndTodayCurrentTimeZone(): Today {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val todayStart = now.date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val todayEnd = todayStart + 86_400_000

        return Today(todayStart, todayEnd)
    }

    fun startEndTodayInUTC(timeZone: TimeZone = TimeZone.currentSystemDefault()): Today {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val todayStartLocal = now.date.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val todayEndLocal = todayStartLocal + 86_400_000

        return Today(todayStartLocal, todayEndLocal)
    }

    val dayOfWeekName: String
        get() =  Weekday.entries[today.dayOfWeek.ordinal].displayName

    val dayOfWeek: Int
        get() = today.dayOfWeek.ordinal

    val todayDateString: String
        get() = today.dayOfMonth.toString().padStart(2,'0')
}

val Clock.System.utcMillis: Long
    get() = this.now().toEpochMilliseconds()

val Long.toCurrentTimeZone: LocalDateTime
    get() {
        val instant = Instant.fromEpochMilliseconds(this)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

val LocalDate.toCurrentLocalDateFromUTC: LocalDate
    get()  {
        val startOfDayInUtc = this.atStartOfDayIn(TimeZone.UTC)
        val localDateTime = startOfDayInUtc.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.date
    }

fun Long.toLocalTimeStamp(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    val utcInstant = Instant.fromEpochMilliseconds(this)
    val localDateTime = utcInstant.toLocalDateTime(timeZone)
    return localDateTime.toInstant(timeZone).toEpochMilliseconds()
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

fun Long.isTimestampOnDate(date: LocalDate, timeZone: TimeZone = TimeZone.UTC): Boolean {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateFromTimestamp = instant.toLocalDateTime(timeZone).date
    return localDateFromTimestamp == date
}

fun Long.toUTC(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    return instant.toString() // Gibt das ISO 8601-Format in UTC zurück
}

fun LocalDateTime.toEpochMillis(timeZone: TimeZone = TimeZone.UTC): Long {
    val instant = this.toInstant(timeZone)
    return instant.toEpochMilliseconds()
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