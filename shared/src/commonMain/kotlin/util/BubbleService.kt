package util

import kotlin.random.Random

object BubbleService {
    fun updateBubbles(deltaTime: Long, bubbles: List<Bubble>, glassHeight: Float): List<Bubble> {
        val bubbleSpeed = 20f // Geschwindigkeit, mit der sich die Blasen nach oben bewegen (fester Wert)
        val updatedBubbles = bubbles.map { bubble ->
            var newY = bubble.y - deltaTime.toFloat() / 1000f * bubbleSpeed // Bewegt die Blasen nach oben

            // Überprüfe, ob die Blase das obere Ende erreicht hat und setze sie zurück
            if (newY + bubble.radius < 0) {
                newY = glassHeight // Setze die Blase nach unten zurück
            }

            // Gib die aktualisierte Blase zurück
            bubble.copy(y = newY)
        }

        return updatedBubbles
    }

    fun generateBubbles(glassWidth: Float, glassHeight: Float, numBubbles: Int): List<Bubble> {
        val newBubbles = mutableListOf<Bubble>()
        for (i in 0 until numBubbles) {
            val x = Random.nextFloat() * (glassWidth - 20f) + 10f // Vermeide Kollision mit dem Rand
            val y = Random.nextFloat() * (glassHeight - 40f) + 20f // Vermeide Kollision oben und unten
            val radius = Random.nextFloat() * 5f + 2f // Zufällige Größe der Blasen
            newBubbles.add(Bubble(x, y, radius))
        }
        return newBubbles
    }

    data class Bubble(
        var x: Float, // X-coordinate of the bubble center
        var y: Float, // Y-coordinate of the bubble center
        val radius: Float, // Radius of the bubble
    )
}