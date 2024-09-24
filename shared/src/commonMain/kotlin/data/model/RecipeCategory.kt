package data.model

enum class RecipeCategory(val categoryId: String, val displayName: String) {
    SNACK("snack", "Snack"),
    HAUPTGERICHT("hauptgericht", "Hauptgericht"),
    BEILAGE("beilage", "Beilage"),
    DESSERT("dessert", "Dessert"),
    FRUEH_PHASE("frueh-phase", "Früh-Phase"), // Phase nach der OP (flüssig)
    PUERIERTE_PHASE("puerierte-phase", "Pürierte Phase"), // Phase mit pürierter Nahrung
    WEICHE_KOST("weiche-kost", "Weiche Kost"), // Weiche, leicht verdauliche Nahrung
    NORMALE_KOST("normale-kost", "Normale Kost"), // Nach der Anpassungsphase
    PROTEINREICH("proteinreich", "Proteinreich"), // Hoher Proteingehalt
    LOW_FAT("low-fat", "Low Fat"), // Niedriger Fettgehalt
    LOW_CARB("low-carb", "Low Carb"); // Niedriger Kohlenhydratgehalt

    companion object {
        // Methode zur Suche einer Kategorie anhand der categoryId
        fun fromStrong(displayName: String): RecipeCategory? {
            return entries.find { it.displayName.equals(displayName, ignoreCase = true) }
        }
    }
}