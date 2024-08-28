package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.WeightDao
import data.local.entitiy.Weight
import data.model.DailyAverage
import data.model.MonthlyAverage
import data.model.WeeklyAverage
import data.network.syncManager.WeightSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.offsetAt
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.lighthousegames.logging.logging
import util.toLocalDate

class WeightRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {

    private var localService: WeightDao = LocalDataSource(db).weight
    private var syncManager: WeightSyncManager = WeightSyncManager(db, serverHost, deviceID)

    suspend fun getAll(): List<Weight> {
        return user?.let { firebaseUser ->
            localService.getAll(firebaseUser.uid).filter { !it.isDeleted }
        } ?: emptyList()
    }

    suspend fun getById(weightId: String): Weight? = this.localService.getById(weightId)

    suspend fun insertOrUpdate(weight: Weight) = this.localService.insertOrUpdate(weight)

    suspend fun insertOrUpdate(weights: List<Weight>) {
        weights.forEach {
            this.localService.insertOrUpdate(it.copy(updatedAt = Clock.System.now().toEpochMilliseconds().toString()))
        }
    }

    suspend fun updateMany(weights: List<Weight>) {
        val toUpdate = weights.map { it.copy(updatedAt = Clock.System.now().toEpochMilliseconds().toString()) }
        localService.updateMany(toUpdate)
    }

    suspend fun softDeleteMany(weights: List<Weight>) {
        val toUpdate = weights.map { it.copy(isDeleted = true) }
        localService.softDeleteMany(toUpdate)
    }

    suspend fun softDeleteById(weightId: String) {
        localService.softDeleteById(weightId)
    }

    suspend fun hardDeleteAllByUserId() {
        user?.let {
            localService.hardDeleteAllByUserId(it.uid)
        }
    }

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<Weight> {
        return user?.let {
            localService.getAllAfterTimeStamp(
                timeStamp,
                it.uid
            )
        } ?: emptyList()
    }

    suspend fun getLastWeight(): Weight? {
        return user?.let {
            localService.getLastWeightFromUserId(it.uid)
        }
    }

    suspend fun syncDataWithRemote() {
        syncManager.syncWeights()
    }

    suspend fun getAverageWeightLastDays(days: Int = 7): List<DailyAverage> {
        val endDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = endDate.minus(days - 1, DateTimeUnit.DAY)

        val timezoneOffset = TimeZone.currentSystemDefault().offsetAt(Clock.System.now()).totalSeconds

        val weightsFromDb = localService.getAverageWeightLastDays(days, startDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds())

        val dayList = mutableListOf<DailyAverage>()

        (0 until days).forEach { date ->
            val generatedDate = startDate.plus(date, DateTimeUnit.DAY)
            val month = generatedDate.monthNumber.toString().padStart(2, '0')
            val day = generatedDate.dayOfMonth.toString().padStart(2, '0')

            val dateExists = weightsFromDb.firstOrNull { it.date.toLocalDate() == generatedDate }

            dayList.add(
                DailyAverage(
                    dateExists?.avgValue ?: 0.0,
                    "$day.$month"
                )
            )
        }

        return dayList
    }

    suspend fun getAverageWeightLastWeeks(weeks: Int = 8): List<WeeklyAverage> {
        val endDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = endDate.minus(7 * weeks - 1, DateTimeUnit.DAY)
        val startOfWeek = startDate.minus((startDate.dayOfWeek.isoDayNumber - 1), DateTimeUnit.DAY)
        val weightsFromDb = localService.getAverageWeightLastWeeks(weeks, startOfWeek.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds())

        val allWeeks = (0 until weeks).map { startOfWeek.plus(7 * it, DateTimeUnit.DAY) }
        val weightsMap = weightsFromDb.associateBy {
            val yearWeek = it.week.split("-")
            LocalDate(yearWeek[0].toInt(), 1, 1).plus((yearWeek[1].toInt() * 7), DateTimeUnit.DAY)
        }
        val completeWeights = allWeeks.map { weekStartDate ->
            weightsMap[weekStartDate]
                ?: WeeklyAverage(
                    0.0,
                    weekStartDate.dayOfMonth.toString() + "." +weekStartDate.monthNumber.toString() + "." + weekStartDate.year.toString(),
                )
        }

        return completeWeights
    }

    suspend fun getAverageWeightLastMonths(months: Int = 8): List<MonthlyAverage> {

        val endDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val startDate = endDate.minus(months - 1, DateTimeUnit.MONTH)

        val weightsFromDb = localService.getAverageWeightLastMonths(months, startDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds())

        // Generate default values for missing months
        val allMonths = (0 until months).map { startDate.plus(it, DateTimeUnit.MONTH) }
        val weightsMap = weightsFromDb.associateBy {
            val yearMonth = it.month.split("-")
            LocalDate(yearMonth[0].toInt(), yearMonth[1].toInt(), 1)
        }

        val completeWeights = allMonths.map { monthStartDate ->
            val weightFromDb = weightsMap[monthStartDate]
            weightsMap[monthStartDate]
                ?: MonthlyAverage(
                    0.0,
                    monthStartDate.monthNumber.toString() + "." + monthStartDate.year.toString()
                )
        }

        return completeWeights
    }
}