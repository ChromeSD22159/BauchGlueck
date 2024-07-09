class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hola, ${platform.name}!"
    }
}