package data.local

import data.local.entitiy.CountdownTimer

interface CountdownTimerRepository {
    suspend fun getTimers(refresh: Boolean): List<CountdownTimer>
    suspend fun updateTimer(timer: CountdownTimer)
    suspend fun deleteTimer(timer: CountdownTimer)
}

class CountdownTimerRepositoryImpl(
    private val db: LocalDatabase
) : CountdownTimerRepository {
    private val localDataSource: LocalDataSource = LocalDataSourceImpl(db)
    override suspend fun getTimers(refresh: Boolean): List<CountdownTimer> {
        return localDataSource.getAllTimer()
    }

    override suspend fun updateTimer(timer: CountdownTimer) {
        localDataSource.updateTimer(timer)
    }

    override suspend fun deleteTimer(timer: CountdownTimer) {
        localDataSource.deleteTimer(timer)
    }

}
