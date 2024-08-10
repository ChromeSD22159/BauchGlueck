package data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

fun iOSHttpClientEngine(): HttpClientEngine {
    return Darwin.create()
}

actual fun createHttpClientEngine(): HttpClientEngine = iOSHttpClientEngine()