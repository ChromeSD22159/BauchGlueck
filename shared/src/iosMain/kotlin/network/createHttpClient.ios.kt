package network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice

fun iOSHttpClientEngine(): HttpClientEngine {
    return Darwin.create()
}

actual fun createHttpClientEngine(): HttpClientEngine = iOSHttpClientEngine()