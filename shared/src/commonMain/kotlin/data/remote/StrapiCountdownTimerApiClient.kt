package data.remote

import data.local.entitiy.CountdownTimer
import data.network.BaseApiEndpoint
import data.network.createHttpClient
import data.remote.model.SyncResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result
import viewModel.apiCall
import viewModel.generateRequestURL
import viewModel.replacePlaceholders

class StrapiCountdownTimerApiClient(
    private val serverHost: String,
    private val httpClient: HttpClient = createHttpClient()
): StrapiSyncApi<CountdownTimer, SyncResponse> {

    enum class ApiEndpoint(override var urlPath: String, override val method: HttpMethod): BaseApiEndpoint {
        COUNTDOWN_TIMER_UPDATE_REMOTE_DATA("/api/timer/updateRemoteData", HttpMethod.Post),
        COUNTDOWN_TIMER_FETCH_TIMERS_AFTER_TIMESTAMP("/api/timer/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}", HttpMethod.Get)
    }

    override suspend fun updateRemoteData(entities: List<CountdownTimer>): Result<SyncResponse, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_UPDATE_REMOTE_DATA

        val response = try {
            httpClient.post {
                url("${serverHost}${endpoint.urlPath}")
                contentType(ContentType.Application.Json)
                setBody(entities)
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (response.status.value) {
            in 200..299 -> {
                try {
                    Result.Success(response.body())
                } catch (e: SerializationException) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            430 -> Result.Error(NetworkError.NOTING_TO_SYNC)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun fetchItemsAfterTimestamp(timestamp: Long, userID: String): Result<List<CountdownTimer>, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_FETCH_TIMERS_AFTER_TIMESTAMP
        endpoint.replacePlaceholders("{timestamp}", timestamp.toString())
        endpoint.replacePlaceholders("{userID}", userID)
        endpoint.generateRequestURL(serverHost)

        return  apiCall<List<CountdownTimer>>(endpoint.urlPath , httpClient, timestamp)
    }
}