package util

fun generateId(): String {
    val chars = ('A'..'Z') + ('0'..'9')
    val blocks = List(4) {
        List(4) { chars.random() }.joinToString("")
    }
    return blocks.joinToString("-")
}