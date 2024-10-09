package data.network

import di.serverHost
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import org.lighthousegames.logging.logging
import util.debugJsonHelper

suspend fun isServerReachable(): Result<String> {
    val url = "$serverHost/api/currentTimeStamp"
    val httpClient: HttpClient = createHttpClient()

    return try {
        val response =  httpClient.get {
            url(url)
        }

        logging().info {
            "response: ${response.toString()}"
        }

        if (response.status == HttpStatusCode.OK) {
            Result.success("Server is reachable")
        } else {
            throw Exception("Server is not reachable")
        }
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
