package network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual fun createHttpClientEngine(): HttpClientEngine {
    return OkHttp.create()
}