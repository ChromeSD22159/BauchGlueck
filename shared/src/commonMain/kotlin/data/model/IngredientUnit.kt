package data.model

enum class IngredientUnit(val displayName: String, val unit: String) {
    Gramm("Gramm", "g"),
    Kilogramm("Kilogramm", "kg"),
    Liter("Liter", "l"),
    Milliliter("Milliliter", "ml"),
    Stueck("Stück", "Stück"),
    EL("EL", "EL"),
    TL("TL", "TL"),
    Prise("Prise", "Prise");

    companion object {
        // Methode zur Suche einer Kategorie anhand der categoryId
        fun fromStrong(string: String): IngredientUnit? {
            return entries.find { it.displayName.equals(string, ignoreCase = true) }
        }

        fun fromUnitString(string: String): IngredientUnit? {
            return entries.find { it.unit.equals(string, ignoreCase = true) }
        }
    }
}