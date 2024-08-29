package data.remote

import data.local.entitiy.CountdownTimer
import data.network.createHttpClient
import data.remote.model.SyncResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.datetime.Clock
import kotlinx.serialization.SerializationException
import org.lighthousegames.logging.logging
import util.NetworkError
import util.Result

class StrapiCountdownTimerApiClient(
    private val serverHost: String
): StrapiSyncApi<CountdownTimer, SyncResponse> {

    private val httpClient: HttpClient = createHttpClient()

    enum class ApiEndpoint(var urlPath: String, val method: HttpMethod) {
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
        endpoint.urlPath = endpoint.urlPath.replace("{timestamp}", timestamp.toString()).replace("{userID}", userID)
        return apiCall(endpoint, timestamp)
    }

    private suspend inline fun <reified T> apiCall(endpoint: ApiEndpoint, body: Any? = null): Result<T, NetworkError> {
        logging().info { "Api Request: ${serverHost}${endpoint.urlPath}" }
        logging().info { "Current Data as TimeStamp: ${Clock.System.now().toEpochMilliseconds()}" }
        val response = try {
            when (endpoint.method) {
                HttpMethod.Get -> httpClient.get {
                    url("${serverHost}${endpoint.urlPath}")
                }
                HttpMethod.Post -> httpClient.post {
                    url("${serverHost}${endpoint.urlPath}")
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                HttpMethod.Put -> httpClient.put {
                    url("${serverHost}${endpoint.urlPath}")
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                HttpMethod.Delete -> httpClient.delete {
                    url("${serverHost}${endpoint.urlPath}")
                }
                else -> throw UnsupportedOperationException("Unsupported HTTP method")
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return handleResult<T>(response)
    }

    private suspend inline fun <reified T> handleResult(response: HttpResponse): Result<T, NetworkError> {
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
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}