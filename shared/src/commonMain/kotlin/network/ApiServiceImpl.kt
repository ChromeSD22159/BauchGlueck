package network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import model.recipe.MeasurementUnit
import model.recipe.Recipe
import model.recipe.RecipeCategory


class ApiServiceImpl: ApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private val baseUrl: BaseUrls = BaseUrls.DEV_SERVER

    override suspend fun fetchRecipeCategories(): List<RecipeCategory> {
        val response: HttpResponse = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = this@ApiServiceImpl.baseUrl.url
                path(Endpoints.CATEGORIES.path)
            }
        }

        return response.body()
    }

    override suspend fun fetchMeasurementUnits(): List<MeasurementUnit> {
        val response: HttpResponse = client.get {
            url {
                protocol = URLProtocol.HTTPS
                path(Endpoints.MEASUREMENT_UNITS.path)
                path("/recipes")
            }
        }

        return response.body()
    }

    override suspend fun fetchRecipes(): List<Recipe> {
        val response: HttpResponse = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = this@ApiServiceImpl.baseUrl.url
                path(Endpoints.RECIPES.path)
            }
        }

        return response.body()
    }

    override suspend fun addRecipe(recipe: Recipe): Recipe? {
        val response: HttpResponse = client.post {
            url {
                protocol = URLProtocol.HTTPS
                host = this@ApiServiceImpl.baseUrl.url
                path(Endpoints.RECIPE_ADD.path)
            }
        }

        return if(response.status.value == 201) {
            return response.body()
        } else null
    }

    override suspend fun updateRecipe(recipe: Recipe): Recipe? {
        val response: HttpResponse = client.post {
            url {
                protocol = URLProtocol.HTTPS
                host = this@ApiServiceImpl.baseUrl.url
                path(Endpoints.RECIPE_UPDATE.path)
            }
        }
        return if(response.status.value == 200) {
            return response.body()
        } else null
    }

    override suspend fun deleteRecipe(id: Int): Boolean {
        val response: HttpResponse = client.delete {
            url {
                protocol = URLProtocol.HTTPS
                host = this@ApiServiceImpl.baseUrl.url
                path(Endpoints.RECIPE_DELETE.path)
            }
        }

        return response.status.value == 200
    }

    private enum class Endpoints(val path: String) {
        RECIPES("/recipes"),
        RECIPE("/recipes/{id}"),
        RECIPE_ADD("/recipes"),
        RECIPE_UPDATE("/recipes/{id}"),
        RECIPE_DELETE("/recipes/{id}"),
        CATEGORIES("/recipe-categories"), // TODO: Replace with actual endpoint
        MEASUREMENT_UNITS("/measurement-units") // TODO: Replace with actual endpoint
    }

    private enum class BaseUrls(val url: String) {
        PRODUCTION_SERVER("https://api.frederikkohler.de/"),
        DEV_SERVER("http://192.168.178.32:8080/")
    }
}
