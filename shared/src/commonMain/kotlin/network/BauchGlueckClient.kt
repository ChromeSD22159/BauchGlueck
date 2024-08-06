package network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import model.recipe.MeasurementUnit
import model.recipe.RecipeCategory
import util.NetworkError
import util.Result

class BauchGlueckClient(
    private val httpClient: HttpClient
) {
    private val serverHost = ServerHost.LOCAL_FREDERIK.url

    suspend fun getMeasurementUnits(
        serverPath: String = ServerPath.MEASUREMENT_UNITS_GET.path
    ): Result<List<MeasurementUnit>, NetworkError> {
        val response = try {
            httpClient.get(
                urlString = "${serverHost}${serverPath}"
            )
        } catch(e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when(response.status.value) {
            in 200..299 -> Result.Success(response.body<List<MeasurementUnit>>())
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun getRecipeCategories(
        serverPath: String = ServerPath.RECIPE_CATEGORIES_GET.path
    ): Result<List<RecipeCategory>, NetworkError> {
        val response = try {
            httpClient.get(
                urlString = "${serverHost}${serverPath}"
            )
        } catch(e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when(response.status.value) {
            in 200..299 -> {
                val censoredText = response.body<List<RecipeCategory>>()
                Result.Success(censoredText)
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

enum class ServerHost(val url: String) {
    LOCAL_FREDERIK("http://192.168.0.73:8080/"),
    PRODUCTION("https://api.frederikkohler.de/bauchglueck/")
}

enum class ServerPath(val path: String) {
    MEASUREMENT_UNITS_GET("measurementUnits/"),
    RECIPE_CATEGORIES_GET("recipeCategories/"),
    RECIPE_CATEGORIES_ADD("recipeCategory"),
    RECIPE_CATEGORIES_UPDATE("recipeCategory/{id}"),
    RECIPE_CATEGORIES_DELETE("recipeCategory/{id}"),
    GET_RECIPES("recipes/"),
    RECIPES("recipes/"),
    RECIPE("{id}"),
    RECIPE_ADD("recipe"),
    RECIPE_UPDATE("recipe/{id}"),
    RECIPE_DELETE("recipe/{id}")
}