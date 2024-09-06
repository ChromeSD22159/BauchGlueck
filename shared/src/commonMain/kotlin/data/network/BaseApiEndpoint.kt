package data.network

import io.ktor.http.HttpMethod

interface BaseApiEndpoint {
    var urlPath: String
    val method: HttpMethod

    fun generateRequestURL(serverHost: String): String {
        return "${serverHost}${this.urlPath}"
    }
}

inline fun <reified T : BaseApiEndpoint> T.replacePlaceholders(placeholder: String, value: String) {
    this.urlPath = this.urlPath.replace(placeholder, value)
}