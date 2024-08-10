package data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

fun androidHttpClientEngine(): HttpClientEngine {
    return OkHttp.create()
}

actual fun createHttpClientEngine(): HttpClientEngine = androidHttpClientEngine()