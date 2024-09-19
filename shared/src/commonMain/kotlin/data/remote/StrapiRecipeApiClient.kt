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

class StrapiRecipeApiClient(
    override val serverHost: String = BuildKonfig.API_HOST,
    override val httpClient: HttpClient = createHttpClient()
): BaseApiClient()