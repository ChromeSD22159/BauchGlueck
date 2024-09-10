package data.remote

import data.network.createHttpClient
import de.frederikkohler.bauchglueck.shared.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Part(val text: String)

@Serializable
data class Error(val message: String)

@Serializable
data class GenerateContentResponse(val error: Error? = null, val candidates: List<Candidate>? = null)

@Serializable
data class GenerateContentRequest(val contents: Content)

@Serializable
data class Content(
    val parts: List<Part>,
    val role: String
)

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
)

@Serializable
data class Candidate(
    val content: Content,
    val finishReason: String,
    val index: Int,
    val safetyRatings: List<SafetyRating>
)

class GeminiApiClient(
    private val baseUrl: String = BuildKonfig.GEMINI_API_HOST,
    private val apiKey: String = BuildKonfig.GEMINI_API_KEY,
    private val httpClient: HttpClient = createHttpClient(),
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false})
        }
    }

    suspend fun generateContent(prompt: String): String? {
        val part = Part(text = prompt)
        val contents = Content( parts = listOf(part) , role = "user")
        val request = GenerateContentRequest(contents)

        try {
            val result: GenerateContentResponse = client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                url { parameters.append("key", apiKey) }
                setBody(request)
            }.body<GenerateContentResponse>()

            return if (result.candidates != null) {
                result.candidates[0].content.parts[0].text
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }
    }
}