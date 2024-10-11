package ui.components.theme

import androidx.compose.runtime.Composable
import coil3.compose.LocalPlatformContext
import coil3.network.ktor.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import data.network.createHttpClient

@Composable
fun coilImageRequest(url: String): ImageRequest {
    val client = createHttpClient()
    return ImageRequest.Builder(LocalPlatformContext.current)
        .fetcherFactory(
            KtorNetworkFetcherFactory(client)
        )
        .data(url)
        .crossfade(true)
        .build()
}