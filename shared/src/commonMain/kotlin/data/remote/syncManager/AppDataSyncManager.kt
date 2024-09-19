package data.remote.syncManager

import data.remote.StrapiApiClient
import data.remote.model.ApiAppStatistics
import data.remote.model.ApiChangeLog
import di.serverHost
import util.NetworkError
import util.Result

class AppDataSyncManager(): BaseSyncManager() {
    private val apiService: StrapiApiClient = StrapiApiClient()

    suspend fun fetchChangeLog(): Result<List<ApiChangeLog>, NetworkError> {
        return apiService.fetchChangeLog()
    }

    suspend fun fetchAppStatistics(): Result<ApiAppStatistics, NetworkError> {
        return apiService.fetchAppStatistics()
    }
}
