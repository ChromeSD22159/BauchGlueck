package util

object UUID {
    fun randomUUID(): String {
        return generateUUID()
    }

    private fun generateUUID(): String {
        val chars = ('a'..'z') + ('0'..'9') + ('A'..'Z')
        val listBlocks = listOf(6, 4, 4, 4, 12)

        return listBlocks.generateBlock { chars }
    }

    private fun List<Int>.generateBlock(chars: () -> List<Char>): String {
        return this.joinToString("-") {
            it.generateBlock {
                chars()
            }
        }
    }

    private fun Int.generateBlock(chars: () -> List<Char>): String {
        return List(this) { chars().random() }.joinToString("")
    }
}