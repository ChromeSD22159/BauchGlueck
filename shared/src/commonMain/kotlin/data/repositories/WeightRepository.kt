package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.WeightDao
import data.local.entitiy.Weight
import data.model.DailyAverage
import data.remote.syncManager.WeightSyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.offsetAt
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import util.toLocalDate

class WeightRepository(
    db: LocalDatabase,
    var serverHost: String,
    var deviceID: String
): BaseRepository() {

    private var localService: WeightDao = LocalDataSource(db).weight
    private var syncManager: WeightSyncManager = WeightSyncManager(db, serverHost, deviceID)

    suspend fun getAll(): List<Weight> {
        val currentUserId = userId ?: return emptyList()
        return localService.getAll(currentUserId).filter { !it.isDeleted }
    }

    fun getAllAsFlow(): Flow<List<Weight>> {
        val currentUserId = userId ?: return emptyFlow()
        return this.localService.getAllAsFlow(currentUserId)
    }

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
        val currentUserId = userId ?: return
        localService.hardDeleteAllByUserId(currentUserId)
    }

    suspend fun getAllAfterTimeStamp(timeStamp: Long): List<Weight> {
        val currentUserId = userId ?: return emptyList()
        return  localService.getAllAfterTimeStamp(
            timeStamp,
            currentUserId
        )
    }

    fun getLastWeight(): Flow<Weight?> {
        val currentUserId = userId ?: return emptyFlow()
        return localService.getLastWeightFromUserId(currentUserId)
    }

    suspend fun syncDataWithRemote() {
        syncManager.syncWeights()
    }

    suspend fun getAverageWeightLastDays(days: Int = 7): List<DailyAverage> {
        val currentUserId = userId ?: return emptyList()

        val endDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = endDate.minus(days - 1, DateTimeUnit.DAY)

        val timezoneOffset = TimeZone.currentSystemDefault().offsetAt(Clock.System.now()).totalSeconds

        val weightsFromDb = localService.getAverageWeightLastDays(
            days = days,
            startDate = startDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
            userId = currentUserId,
        )

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