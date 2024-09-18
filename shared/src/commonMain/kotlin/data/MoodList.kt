package data

import data.model.Mood

data object Moods {
    val list = listOf(
        Mood("\uD83E\uDD74 Angeschlagen"),
        Mood("Allergie"),
        Mood("Energieschub"),
        Mood("☹\uFE0F Depressiv"),
    )
}