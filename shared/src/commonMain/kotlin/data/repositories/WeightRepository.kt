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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
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

    fun getAllAsFlow() = this.localService.getAllAsFlow()

    suspend fun getById(weightId: String): Weight? = this.localService.getById(weightId)

    suspend fun insert(weight: Weight) = this.localService.insert(weight)

    suspend fun insert(weights: List<Weight>) {
        weights.forEach {
            this.localService.insert(it.copy(updatedAt = Clock.System.now().toEpochMilliseconds().toString()))
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

    fun getLastWeight(): Flow<Weight?> {
        if (user == null) return flowOf(null)
        return localService.getLastWeightFromUserId(user!!.uid)
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
}