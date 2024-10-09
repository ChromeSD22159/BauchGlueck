package util

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient

fun androidHttpClientEngine(): HttpClientEngine {
    val client = OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
        .build()

    return OkHttp.create {
        preconfigured = client
    }
}

actual fun createHttpClientEngine(): HttpClientEngine = androidHttpClientEngine()