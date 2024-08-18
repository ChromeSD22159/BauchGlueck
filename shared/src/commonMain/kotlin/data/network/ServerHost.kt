package data.network

enum class ServerHost(val url: String) {
    LOCAL_FREDERIK("http://192.168.0.73:1337"),
    LOCAL_SABINA("http://192.168.1.57:1337"),
    PRODUCTION("https://api.frederikkohler.de/bauchglueck")
}