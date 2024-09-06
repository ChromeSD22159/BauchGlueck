package viewModel

import data.Repository
import data.remote.model.ApiRecipesResponse
import data.network.BaseApiEndpoint
import data.network.createHttpClient
import de.frederikkohler.bauchglueck.shared.BuildKonfig
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerializationException
import org.lighthousegames.logging.logging
import util.NetworkError
import util.Result
import util.debugJsonHelper
import util.onError
import util.onSuccess

class RecipeViewModel(
    private val repository: Repository,
): ViewModel() {
    private val scope = viewModelScope

    fun fetchRecipes() {
        scope.launch {
            val result = repository.recipeRepository.getRecipesOverview(5)

            result.onSuccess {
                debugJsonHelper(it)
            }.onError {
                debugJsonHelper(it)
            }
        }
    }
}

class StrapiRecipeApiClient {
    private val serverHost: String = BuildKonfig.API_HOST
    private val httpClient: HttpClient = createHttpClient()

    enum class ApiEndpoint(override var urlPath: String, override val method: HttpMethod): BaseApiEndpoint {
        RECIPES_OVERVIEW_REMOTE_DATA("/api/recipes/overview?count={count}", HttpMethod.Get)
    }

    suspend fun getRecipesOverview(maxCount: Int?): Result<List<ApiRecipesResponse>, NetworkError> {
        val endpoint = ApiEndpoint.RECIPES_OVERVIEW_REMOTE_DATA
        endpoint.generateRequestURL(serverHost)
        endpoint.replacePlaceholders("{count}", maxCount.toString())

        return apiCall( endpoint.urlPath , httpClient)
    }
}



inline fun <reified T : BaseApiEndpoint> T.generateRequestURL(serverHost: String) {
    this.urlPath = "${serverHost}${this.urlPath}"
}

inline fun <reified T : BaseApiEndpoint> T.replacePlaceholders(placeholder: String, value: String) {
    this.urlPath = this.urlPath.replace(placeholder, value)
}

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