package util

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Protocol
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

fun androidHttpClientEngine(): HttpClientEngine {

    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val trustManager = trustManagerFactory.trustManagers
        .filterIsInstance<X509TrustManager>()
        .firstOrNull() ?: throw IllegalStateException("No X509TrustManager found")

    val sslContext = SSLContext.getInstance("TLSv1.3").apply {
        init(null, arrayOf(trustManager), null)
    }

    val client = OkHttpClient.Builder()
        .sslSocketFactory(sslContext.socketFactory, trustManager)
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .build()

    return OkHttp.create {
        preconfigured = client
    }
}

actual fun createHttpClientEngine(): HttpClientEngine = androidHttpClientEngine()



// openssl s_client -connect bauchglueck.appsbyfrederikkohler.de/api/currentTimeStamp