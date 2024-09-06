package data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.datetime.Clock
import kotlinx.serialization.SerializationException
import org.lighthousegames.logging.logging
import util.NetworkError
import util.Result

open class BaseApiClient()  {
    suspend inline fun <reified T> apiCall(endpoint: String, httpClient: HttpClient, timestamp: Long? = null): Result<T, NetworkError> {
        logging().info { "Api Request: $endpoint" }
        logging().info { "Current Data as TimeStamp: ${Clock.System.now().toEpochMilliseconds()}" }
        val response = try {
            httpClient.get {
                url(endpoint)
            }
        } catch (e: NoTransformationFoundException) {
            return Result.Error(NetworkError.NOTING_TO_SYNC)
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        logging().info { "$endpoint -> ${response.status}" }

        return handleResult<T>(response)
    }

    suspend inline fun <reified T> handleResult(response: HttpResponse): Result<T, NetworkError> {
        return when (response.status.value) {
            in 200..299 -> {
                try {
                    Result.Success(response.body())
                } catch (e: SerializationException) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            430 -> Result.Error(NetworkError.NOTING_TO_SYNC)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}