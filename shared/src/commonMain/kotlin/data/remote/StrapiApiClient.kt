package data.remote

import data.model.RecipeCategory
import data.network.BaseApiEndpoint
import data.network.createHttpClient
import data.network.replacePlaceholders
import data.remote.model.ApiChangeLog
import data.remote.model.ApiRecipesResponse
import data.remote.model.ApiAppStatistics
import data.remote.model.ApiBackendStatistic
import data.remote.model.ApiUploadImageResponse
import data.remote.model.RecipeUpload
import de.frederikkohler.bauchglueck.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import util.FirebaseCloudMessagingResponse
import util.NetworkError
import util.NotificationCronJobRequest
import util.Result
import util.UUID

class StrapiApiClient(
    override val httpClient: HttpClient = createHttpClient()
) : BaseApiClient()

open class BaseApiClient(
    open val httpClient: HttpClient = createHttpClient()
) {
    open val serverHost = BuildKonfig.API_HOST

    enum class UpdateRemoteEndpoint(override var urlPath: String, override val method: HttpMethod): BaseApiEndpoint {
        WATER_INTAKE("/api/water-intake/updateRemoteData", HttpMethod.Post),
        WEIGHT("/api/weight/updateRemoteData", HttpMethod.Post),
        MEDICATION("/api/medication/syncDeviceMedicationData", HttpMethod.Post),
        COUNTDOWN_TIMER("/api/timer/updateRemoteData", HttpMethod.Post),
    }

    enum class FetchAfterTimestampEndpoint(override var urlPath: String, override val method: HttpMethod): BaseApiEndpoint {
        WATER_INTAKE("/api/water-intake/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        Recipe("/api/recipes/getUpdatedRecipesEntries?timeStamp={timestamp}", HttpMethod.Get),
        WEIGHT("/api/weight/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        MEDICATION("/api/medication/getUpdatedMedicationEntries?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        COUNTDOWN_TIMER("/api/timer/fetchItemsAfterTimeStamp?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        MealPlan("/api/mealPlan/getUpdatedMealPlanDayEntries?timeStamp={timestamp}&userId={userID}", HttpMethod.Get),
        GenerateRecipe("/api/recipes/generateRecipe?category={RecipeKind}", HttpMethod.Get),
        LoadAllRecipes("/api/meal/getUpdatedMealEntries?timeStamp={timestamp}", HttpMethod.Get)
    }

    enum class RemoteNotificationEndpoint(override var urlPath: String, override val method: HttpMethod): BaseApiEndpoint {
        SEND_NOTIFICATION("/api/send-notification", HttpMethod.Post),
        SCHEDULE_NOTIFICATION("/api/send-schedule-notification", HttpMethod.Post),
        UPLOAD_IMAGE("/api/upload/", HttpMethod.Post),
        APP_Statistic("/api/appStatistics", HttpMethod.Get)
    }

    // TODO check MealPlan Routes AND SyncLogic
    // TODO check ShoppingList Routes AND SyncLogic

    enum class ApiEndpoint(override var urlPath: String, override val method: HttpMethod): BaseApiEndpoint {
        RECIPES_OVERVIEW_REMOTE_DATA("/api/recipes/overview?count={count}", HttpMethod.Get),
        SEARCH_RECIPES("/api/recipes/searchRecipes?userId={userID}", HttpMethod.Get),
        STARTUP_MEALS("/api/getStartUpMeals", HttpMethod.Get),
        STARTUP_MEALS_COUNT("/api/getStartUpMealsCount", HttpMethod.Get),
        ChangeLog("/api/changeLog", HttpMethod.Get),
        AppStatistics("/api/appStatistics", HttpMethod.Get),
        SaveDeviceToken("/api/saveDeviceToken", HttpMethod.Post),
        DeleteDeviceToken("/api/deleteDeviceToken", HttpMethod.Post),
        UPLOAD_RECIPE("/api/recipes/createRecipe", HttpMethod.Post)
    }

    suspend fun getBackendStatistics(): Result<ApiBackendStatistic, NetworkError> {
        val endpoint = RemoteNotificationEndpoint.APP_Statistic.generateRequestURL(serverHost)
        return apiCall(endpoint, httpClient)
    }

    suspend fun sendScheduleRemoteNotification(notification: NotificationCronJobRequest): Result<FirebaseCloudMessagingResponse, NetworkError> {
        logging().info { "sendScheduleRemoteNotification" }
        logging().info { notification }
        return sendNotification(RemoteNotificationEndpoint.SCHEDULE_NOTIFICATION, notification)
    }

    suspend fun getRecipesOverview(maxCount: Int?): Result<List<ApiRecipesResponse>, NetworkError> {
        val endpoint = ApiEndpoint.RECIPES_OVERVIEW_REMOTE_DATA
        endpoint.replacePlaceholders("{count}", maxCount.toString())

        return apiCall(endpoint.generateRequestURL(serverHost) , httpClient)
    }

    suspend fun searchRecipes(query: String, userID: String): Result<List<ApiRecipesResponse>, NetworkError> {
        val endpoint = ApiEndpoint.SEARCH_RECIPES
        endpoint.replacePlaceholders("{userID}", userID)

        val url = endpoint.generateRequestURL(serverHost)+"&searchQuery=" + query

        logging().info { "searchRecipes: ${url}" }

        return try {
            val response = httpClient.get(url)
            Result.Success(response.body())
        } catch (e: Exception) {
            println("Fehler bei der Deserialisierung: $e")
            Result.Error(NetworkError.SERIALIZATION)
        }
    }

    suspend fun generateRecipe(category: RecipeCategory): Result<GeneratedRecipeResponse, NetworkError> {
        val endpoint = FetchAfterTimestampEndpoint.GenerateRecipe
        endpoint.replacePlaceholders("{RecipeKind}", category.name)

        return try {
            val response = httpClient.get(endpoint.generateRequestURL(serverHost))
            val generatedRecipe: GeneratedRecipeResponse = Json.decodeFromString(response.body())
            Result.Success(generatedRecipe)
        } catch (e: Exception) {
            println("Fehler bei der Deserialisierung: $e")
            Result.Error(NetworkError.SERIALIZATION)
        }
    }

    suspend fun fetchStartUpMeals(): Result<List<ApiRecipesResponse>, NetworkError> {
        return apiCall(ApiEndpoint.STARTUP_MEALS.generateRequestURL(serverHost) , httpClient)
    }

    suspend fun fetchStartUpMealsCount(): Result<LengthResponse, NetworkError> {
        return apiCall(ApiEndpoint.STARTUP_MEALS_COUNT.generateRequestURL(serverHost) , httpClient)
    }

    suspend fun fetchChangeLog(): Result<List<ApiChangeLog>, NetworkError> {
        val endpoint = ApiEndpoint.ChangeLog.generateRequestURL(serverHost)
        return apiCall(endpoint, httpClient)
    }

    suspend fun fetchAppStatistics(): Result<ApiAppStatistics, NetworkError> {
        val endpoint = ApiEndpoint.AppStatistics.generateRequestURL(serverHost)
        return apiCall(endpoint, httpClient)
    }

    suspend fun uploadImage(text: String, image: ByteArray): Result<List<ApiUploadImageResponse>, NetworkError> {
        val endpoint = RemoteNotificationEndpoint.UPLOAD_IMAGE.urlPath
        val imageName = UUID.randomUUID().toString()
        val response = try {
            httpClient.submitFormWithBinaryData(
                url = serverHost + endpoint,
                formData = formData {
                    // Example of sending other parameters in the same request
                    append("text", text)

                    // Properly set the content headers for the image file
                    append("files", image, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"files\"; filename=\"$imageName.jpg\"")
                    })
                }
            )
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            logging().info { "!!! !!! !!! $endpoint -> ${e.message}" }
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        return handleResult(response)
    }

    suspend fun uploadRecipe(recipe: RecipeUpload): Result<ApiRecipesResponse, NetworkError> {
        return sendData<RecipeUpload, ApiRecipesResponse>(
            apiEndpoint = ApiEndpoint.UPLOAD_RECIPE,
            entities = recipe
        )
    }

    suspend fun updateRecipes(timestamp: Long): Result<List<ApiRecipesResponse>, NetworkError> {
        val endpoint = FetchAfterTimestampEndpoint.LoadAllRecipes
        endpoint.replacePlaceholders("{timestamp}", timestamp.toString())
        return apiCall(endpoint.generateRequestURL(serverHost), httpClient)
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

    suspend inline fun <reified Q, reified R> sendNotification(
        apiEndpoint: RemoteNotificationEndpoint,
        notification: Q,
    ): Result<R, NetworkError> {
        val response = try {
            httpClient.post {
                url("${serverHost}${apiEndpoint.urlPath}")
                contentType(ContentType.Application.Json)
                setBody(notification)
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            logging().info { "!!! !!! !!! $apiEndpoint -> ${e.message}" }
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        logging().info { "$apiEndpoint -> ${response}" }

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

    suspend inline fun <reified Q, reified R> sendData(
        apiEndpoint: ApiEndpoint,
        entities: Q,
    ): Result<R, NetworkError> {
        logging().info { "sendData: ${serverHost}${apiEndpoint.urlPath}" }
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

        logging().info { "$apiEndpoint -> ${response}" }

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

@Serializable
data class GeneratedRecipeResponse(
    val description: String,
    val fat: Int,
    val ingredients: List<Ingredient>,
    val kcal: Int,
    val name: String,
    val preparation: String,
    val preparationTimeInMinutes: String,
    val protein: Int,
    val sugar: Int,
)

@Serializable
data class Ingredient(
    val name: String,
    val unit: String,
    val value: String,
)
