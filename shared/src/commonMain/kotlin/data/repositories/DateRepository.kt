package data.repositories

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

class DateRepository {
    fun getNextSevenDays(): List<LocalDate> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return (0..7).map { today.plus(it, DateTimeUnit.DAY) }
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
}

val LocalDate.dayOfMonth: Int
    get() = this.dayOfMonth