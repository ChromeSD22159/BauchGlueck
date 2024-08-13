package data.repositories

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
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