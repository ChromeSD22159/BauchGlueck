package data.network

enum class ServerHost(val url: String) {
    LOCAL_FREDERIK("http://192.168.0.73:8080/"),
    LOCAL_SABINA("http://192.168.1.57:8080/"),
    PRODUCTION("https://api.frederikkohler.de/bauchglueck/")
}