package data.network

import de.frederikkohler.bauchglueck.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import util.createHttpClientEngine

fun createHttpClient(

): HttpClient {
    val engine = createHttpClientEngine()
    val apiKey = BuildKonfig.API_KEY
    
    return HttpClient(engine) {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL

        }
        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(apiKey, "xyz111")
                }
            }
        }
    }
}
