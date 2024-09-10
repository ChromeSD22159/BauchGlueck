package data.remote

import data.network.BaseApiEndpoint
import data.network.createHttpClient
import data.network.replacePlaceholders
import data.remote.model.ApiRecipesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import util.NetworkError
import util.Result

class StrapiApiClient(
    override val serverHost: String,
    override val httpClient: HttpClient = createHttpClient()
) : BaseApiClient(serverHost)

open class BaseApiClient(
    open val serverHost: String,
    open val httpClient: HttpClient = createHttpClient()
) {
    enum class UpdateRemoteEndpoint(override var urlPath: String, override val method: HttpMethod):
        BaseApiEndpoint {
        WATER_INTAKE("/api/water-intake/updateRemoteData", HttpMethod.Post),
        WEIGHT("/api/weight/updateRemoteData", HttpMethod.Post),
        MEDICATION("/api/medication/syncDeviceMedicationData", HttpMethod.Post),
        STARTUP_MEALS("/api/weight/updateRemoteData", HttpMethod.Post),
        COUNTDOWN_TIMER("/api/timer/updateRemoteData", HttpMethod.Post),
    }

    enum class FetchAfterTimestampEndpoint(override var urlPath: String, override val method: HttpMethod):
        BaseApiEndpoint {
        WATER_INTAKE("/api/water-intake/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        WEIGHT("/api/weight/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        MEDICATION("/api/medication/getUpdatedMedicationEntries?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        COUNTDOWN_TIMER("/api/timer/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        MealPlan("/api/mealPlan/getUpdatedMealPlanDayEntries?timeStamp={timestamp}&userId={userID}", HttpMethod.Get)
    }

    enum class ApiEndpoint(override var urlPath: String, override val method: HttpMethod):
        BaseApiEndpoint {
        RECIPES_OVERVIEW_REMOTE_DATA("/api/recipes/overview?count={count}", HttpMethod.Get),
        STARTUP_MEALS("/api/getStartUpMeals", HttpMethod.Get),
        STARTUP_MEALS_COUNT("/api/getStartUpMealsCount", HttpMethod.Get),
    }

    suspend fun getRecipesOverview(maxCount: Int?): Result<List<ApiRecipesResponse>, NetworkError> {
        val endpoint = ApiEndpoint.RECIPES_OVERVIEW_REMOTE_DATA
        endpoint.replacePlaceholders("{count}", maxCount.toString())

        return apiCall(endpoint.generateRequestURL(serverHost) , httpClient)
    }

    suspend fun fetchStartUpMeals(): Result<List<ApiRecipesResponse>, NetworkError> {
        return apiCall(ApiEndpoint.STARTUP_MEALS.generateRequestURL(serverHost) , httpClient)
    }

    open suspend fun fetchStartUpMealsCount(): Result<LengthResponse, NetworkError> {
        return apiCall(ApiEndpoint.STARTUP_MEALS_COUNT.generateRequestURL(serverHost) , httpClient)
    }



    /**
     * Macht einen API-Aufruf und versucht, eine Antwort des Typs [T] abzurufen.
     *
     * @param T Der erwartete Rückgabetyp der API-Antwort.
     */
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

    /**
     * Handhabt das Ergebnis des API-Aufrufs.
     *
     * @param T Der erwartete Typ der Antwort, die vom Server zurückgegeben wird.
     */
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

    /**
     * Ruft Items ab, die nach einem bestimmten Zeitstempel geändert wurden.
     *
     * @param T Der erwartete Rückgabetyp (z. B. eine Liste von Items).
     */
    suspend inline fun <reified T> fetchItemsAfterTimestamp(apiEndpoint : FetchAfterTimestampEndpoint, timestamp: Long, userID: String): Result<T, NetworkError> {
        apiEndpoint.replacePlaceholders("{timestamp}", timestamp.toString())
        apiEndpoint.replacePlaceholders("{userID}", userID)

        return apiCall<T>(apiEndpoint.generateRequestURL(serverHost) , httpClient, timestamp)
    }

    /**
     * Aktualisiert Remote-Daten, indem eine Anfrage vom Typ [Q] gesendet wird und eine Antwort vom Typ [R] erwartet wird.
     *
     * @param Q Der Typ der Entität(en), die an die API gesendet werden.
     * @param R Der Typ der Antwort, die von der API erwartet wird.
     */
    suspend inline fun <reified Q, reified R> updateRemoteData(
        apiEndpoint: UpdateRemoteEndpoint,
        entities: Q,
    ): Result<R, NetworkError> {
        val response = try {
            httpClient.post {
                url("${serverHost}${apiEndpoint.urlPath}")
                contentType(ContentType.Application.Json)
                setBody(entities)
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            logging().info { "!!! !!! !!! $apiEndpoint -> ${e.message}" }
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return when (response.status.value) {
            in 200..299 -> {
                try {
                    Result.Success(response.body<R>())
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
}

@Serializable
data class LengthResponse(
    val length: Int
)

