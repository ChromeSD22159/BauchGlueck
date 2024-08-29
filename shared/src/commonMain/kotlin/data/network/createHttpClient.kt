package data.network

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

val apiToken = "d2978348e50d8c3ec898c7dc8e0af323a3e4e65d617d3e31b5474a6c299802c653febf5b87edf645c73a8c0149684dd03d000f3bc83fc771d254c37858e7e0d92076a350f1755ae5acac4d5e04d439b95426125c8a3fd4e622c8c11e88699ed2182d1774ab30f76af70318f064862ff81ee1873d9dfdf86c834021e519b974e0"

fun createHttpClient(): HttpClient {
    val engine = createHttpClientEngine()
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
                    BearerTokens(apiToken, "xyz111")
                }
            }
        }
    }
}
