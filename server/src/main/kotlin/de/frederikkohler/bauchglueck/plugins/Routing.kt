package de.frederikkohler.bauchglueck.plugins

import de.frederikkohler.bauchglueck.utils.EnvService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get

fun Application.configureRouting(
    envManager: EnvService =get(),
) {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    val dotenv = envManager.getEnv()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/api/get-data") {
            call.respond("exchangeRate")
        }
    }
}