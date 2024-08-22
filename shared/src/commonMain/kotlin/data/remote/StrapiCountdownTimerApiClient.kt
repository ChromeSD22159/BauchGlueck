package data.remote

import data.local.entitiy.CountdownTimer
import data.network.createHttpClient
import data.remote.model.CountdownTimerAttributes
import data.remote.model.TimerSyncResponse
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
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result
import util.onError

interface StrapiApi {
    suspend fun getCountdownTimers(userID: String): Result<List<CountdownTimerAttributes>, NetworkError>
    suspend fun getCountdownTimerById(timerId: String): Result<CountdownTimer, NetworkError>
    suspend fun createCountdownTimer(timer: CountdownTimer): Result<CountdownTimer, NetworkError>
    suspend fun updateCountdownTimer(timer: CountdownTimer): Result<CountdownTimer, NetworkError>
    suspend fun deleteCountdownTimer(timerId: String): Result<Unit, NetworkError>
    suspend fun deleteCountdownTimers(timers: List<CountdownTimer>): Result<Unit, NetworkError>
    suspend fun updateOrInsertCountdownTimers(timers: List<CountdownTimer>): Result<List<CountdownTimer>, NetworkError>
    suspend fun syncCountdownTimers(timers: List<CountdownTimer>): Result<TimerSyncResponse, NetworkError>
}

class StrapiCountdownTimerApiClient(
    private val serverHost: String
): StrapiApi {

    private val httpClient: HttpClient = createHttpClient()

    enum class ApiEndpoint(var urlPath: String, val method: HttpMethod) {
        COUNTDOWN_TIMER_GET("/api/countdown-timers/{id}", HttpMethod.Get),
        COUNTDOWN_TIMERS_GET("/api/timer-list?userId={userID}", HttpMethod.Get),
        COUNTDOWN_TIMER_POST("/api/countdown-timers", HttpMethod.Post),
        COUNTDOWN_TIMER_PUT("/api/countdown-timers/{id}", HttpMethod.Put),
        COUNTDOWN_TIMER_DELETE("/api/countdown-timers/{id}", HttpMethod.Delete),
        COUNTDOWN_TIMER_UPDATE_OR_INSERT("/api/countdown-timers/update-or-insert", HttpMethod.Put),
        COUNTDOWN_TIMER_DELETE_TIMER_LIST("/api/countdown-timers/delete-timer-list", HttpMethod.Delete),
        COUNTDOWN_TIMER_SYNC("/api/timer-list/sync", HttpMethod.Post)
    }


    // Timer-API-Impl
    override suspend fun getCountdownTimers(userID: String): Result<List<CountdownTimerAttributes>, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMERS_GET
        endpoint.urlPath = ApiEndpoint.COUNTDOWN_TIMERS_GET.urlPath.replace("{userID}", userID)
        return apiCall(endpoint)
    }

    override suspend fun getCountdownTimerById(timerId: String): Result<CountdownTimer, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_GET
        endpoint.urlPath = ApiEndpoint.COUNTDOWN_TIMER_GET.urlPath.replace("{id}", timerId)
        return apiCall(endpoint)
    }

    override suspend fun createCountdownTimer(timer: CountdownTimer): Result<CountdownTimer, NetworkError> {
        return apiCall(ApiEndpoint.COUNTDOWN_TIMER_POST, timer)
    }

    override suspend fun updateCountdownTimer(timer: CountdownTimer): Result<CountdownTimer, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_PUT
        endpoint.urlPath = ApiEndpoint.COUNTDOWN_TIMER_PUT.urlPath.replace("{id}", timer.timerId)
        return apiCall(endpoint, timer)
    }

    override suspend fun deleteCountdownTimer(timerId: String): Result<Unit, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_DELETE
        endpoint.urlPath = ApiEndpoint.COUNTDOWN_TIMER_DELETE.urlPath.replace("{id}", timerId)
        return apiCall(endpoint)
    }

    override suspend fun updateOrInsertCountdownTimers(timers: List<CountdownTimer>): Result<List<CountdownTimer>, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_UPDATE_OR_INSERT
        return apiCall(endpoint, timers)
    }

    override suspend fun deleteCountdownTimers(timers: List<CountdownTimer>): Result<Unit, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_DELETE_TIMER_LIST
        return apiCall(endpoint, timers)
    }

    override suspend fun syncCountdownTimers(timers: List<CountdownTimer>): Result<TimerSyncResponse, NetworkError> {
        val endpoint = ApiEndpoint.COUNTDOWN_TIMER_SYNC

        val response = try {
            httpClient.get {
                url("${serverHost}${endpoint.urlPath}")
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
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }



    private suspend inline fun <reified T> apiCall(endpoint: ApiEndpoint, body: Any? = null): Result<T, NetworkError> {
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