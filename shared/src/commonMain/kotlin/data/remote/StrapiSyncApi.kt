package data.remote

import util.NetworkError
import util.Result

interface StrapiSyncApi<T, R> {
    suspend fun updateRemoteData(entities: List<T>): Result<R, NetworkError>
    suspend fun fetchItemsAfterTimestamp(timestamp: Long, userID: String): Result<List<T>, NetworkError>
}