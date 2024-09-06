package data.remote

import data.network.BaseApiEndpoint
import data.network.createHttpClient
import data.network.replacePlaceholders
import data.remote.model.ApiRecipesResponse
import de.frederikkohler.bauchglueck.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import util.NetworkError
import util.Result

class StrapiRecipeApiClient: BaseApiClient() {
    private val serverHost: String = BuildKonfig.API_HOST
    private val httpClient: HttpClient = createHttpClient()

    enum class ApiEndpoint(override var urlPath: String, override val method: HttpMethod): BaseApiEndpoint {
        RECIPES_OVERVIEW_REMOTE_DATA("/api/recipes/overview?count={count}", HttpMethod.Get)
    }

    suspend fun getRecipesOverview(maxCount: Int?): Result<List<ApiRecipesResponse>, NetworkError> {
        val endpoint = ApiEndpoint.RECIPES_OVERVIEW_REMOTE_DATA
        endpoint.replacePlaceholders("{count}", maxCount.toString())

        return apiCall(endpoint.generateRequestURL(serverHost) , httpClient)
    }
}