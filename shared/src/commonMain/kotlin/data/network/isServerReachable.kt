package data.network

import di.serverHost
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.lighthousegames.logging.logging
import util.NetworkError

suspend fun isServerReachable(): Result<String> {
    val url = "$serverHost/api/currentTimeStamp"
    val httpClient: HttpClient = createHttpClient()

    return try {
        val response: HttpResponse =  httpClient.get {
            url(url)
        }

        logging().info { response.status }

        if (response.status == HttpStatusCode.OK) {
            Result.success("Server is reachable")
        } else {
            throw Exception("Server is not reachable")
        }
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
