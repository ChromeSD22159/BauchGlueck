package data.network

import di.serverHost
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class TimestampResponse(val timestamp: Long)

suspend fun isServerReachable(): Boolean {
    return withContext(Dispatchers.IO) { // Perform network operations on IO dispatcher
        try {
            val client = HttpClient()
            val response: HttpResponse = client.get("$serverHost/api/currentTimeStamp")
            if (response.status.isSuccess()) {
                val timestampResponse = response.body<TimestampResponse>()
                println("Server timestamp: ${timestampResponse.timestamp}")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., network errors)
            println("Error checking server reachability: ${e.message}")
            false
        }
    }
}