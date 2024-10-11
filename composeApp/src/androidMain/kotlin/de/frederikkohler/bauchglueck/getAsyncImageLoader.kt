package de.frederikkohler.bauchglueck

import android.content.Context
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.LocalPlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.util.CoilUtils
import coil3.util.DebugLogger
import data.network.createHttpClient
import io.ktor.http.ContentType
import okhttp3.OkHttpClient
import util.ApplicationContextHolder.context
import java.io.File
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun getAsyncImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader
        .Builder(context)
        .crossfade(true)
        .logger(DebugLogger())
        .build()
}
