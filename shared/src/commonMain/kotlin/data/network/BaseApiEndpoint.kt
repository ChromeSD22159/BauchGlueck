package data.network

import io.ktor.http.HttpMethod

interface BaseApiEndpoint {
    var urlPath: String
    val method: HttpMethod
}