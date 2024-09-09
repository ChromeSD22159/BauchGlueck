package data.remote

import data.local.entitiy.Weight
import data.network.BaseApiEndpoint
import data.network.createHttpClient
import data.network.replacePlaceholders
import data.remote.model.ApiRecipesResponse
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.lighthousegames.logging.logging
import util.NetworkError
import util.Result

class StrapiMealApiClient(
    private val serverHost: String,
    private val httpClient: HttpClient = createHttpClient()
): BaseApiClient() {

    enum class ApiEndpoint(override var urlPath: String, override val method: HttpMethod):
        BaseApiEndpoint {
        COUNTDOWN_TIMER_UPDATE_REMOTE_DATA("/api/weight/updateRemoteData", HttpMethod.Post),
        STARTUP_MEALS_FETCH_TIMERS_AFTER_TIMESTAMP("/api/getStartUpMeals", HttpMethod.Get),
        STARTUP_MEALS_COUNT("/api/getStartUpMealsCount", HttpMethod.Get),
        STARTUP_MEALS("/api/getStartUpMeals", HttpMethod.Get)
    }

    suspend fun updateRemoteData(entities: List<Weight>): Result<SyncResponse, NetworkError> {
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

        logging().info { "${serverHost}${endpoint.urlPath} -> ${response.status}" }

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

    suspend fun fetchStartUpMeals(timestamp: Long, userID: String): Result<List<ApiRecipesResponse>, NetworkError> {
        return apiCall(ApiEndpoint.STARTUP_MEALS_FETCH_TIMERS_AFTER_TIMESTAMP.generateRequestURL(serverHost), httpClient, timestamp)
    }

    suspend fun fetchStartUpMealsCount(): Result<LengthResponse, NetworkError> {
        return apiCall<LengthResponse>(ApiEndpoint.STARTUP_MEALS_COUNT.generateRequestURL(serverHost) , httpClient)
    }
}

@Serializable
data class LengthResponse(
    val length: Int
)