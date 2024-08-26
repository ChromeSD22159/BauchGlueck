package data.network

import di.serverHost
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.lighthousegames.logging.logging
import util.NetworkError

suspend fun isServerReachable(): Result<String> {
    return try {
        val client = HttpClient()
        val response: HttpResponse = client.get("$serverHost/api/currentTimeStamp")

        if (response.status == HttpStatusCode.OK) {
            Result.success("Server is reachable")
        } else {
            throw Exception("Server is not reachable")
        }
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
