package ui.navigations

sealed class Destination(val route: String, val title: String) {
    data object Launch : Destination("Launch", "Launch")
    data object Login : Destination("Login", "Login")
    data object CreateUserProfile : Destination("CreateUserProfile", "Erstelle dein UserProfile")
    data object SignUp : Destination("Register", "Register")
    data object ForgotPassword : Destination("ForgotPassword", "ForgotPassword")
    data object Home : Destination("Home", "Home")
    data object MealPlanCalendar : Destination("Calendar", "Kalender")
    data object Timer : Destination("Timer", "Timer")
    data object Medication : Destination("Medication", "Medikation")
    data object AddMedication : Destination("AddMedication", "Medikation hinzufügen")
    data object EditMedication : Destination("EditMedication", "Medikation Bearbeiten")
    data object Weight : Destination("Weight", "Gewicht")
    data object ShowAllWeights : Destination("ShowAllWeights", "Alle Gewichtungseinträge")
    data object AddWeight : Destination("AddWeight", "Gewicht hinzufügen")
    data object WaterIntake : Destination("WaterIntake", "Wasseraufnahme")
    data object AddWaterIntake: Destination("AddWaterIntake", "Flüssigkeit hinzufügen")
    data object AddNote: Destination("AddNote", "Notiz hinzufügen")
    data object EditNote: Destination("EditNote", "Notiz Bearbeiten")
    data object ShowAllNotes: Destination("ShowAllNotes", "Alle Notizen")
    data object AddTimer : Destination("AddTimer", "Timer hinzufügen")
    data object EditTimer : Destination("EditTimer", "Timer Bearbeiten")
    data object Recipes : Destination("Recipes", "Rezepte")
    data object SearchRecipe: Destination("SearchRecipe", "Rezept suchen")
    data object RecipeCategories: Destination("RecipeCategories", "Rezepte")
    data object RecipeList: Destination("RecipeList", "Rezepte")
    data object RecipeDetailScreen: Destination("RecipeDetailScreen", "Rezept Detail")
    data object RecipeOverview: Destination("RecipeOverview", "Rezept übersicht")
    data object AddRecipe: Destination("AddRecipe", "Rezept hinzufügen")
    data object EditRecipe: Destination("EditRecipe", "Rezept Bearbeiten")
    data object ShoppingLists: Destination("ShoppingLists", "Shoppinglisten")
    data object ShoppingListDetail: Destination("ShoppingListDetail", "Shoppinglist Detail")
    data object ShoppingListGenerate: Destination("ShoppingListGenerate", "Shopping Liste erstellen")
    data object Settings : Destination("Settings", "Einstellungen")
    data object AdminPanel : Destination("AdminPanel", "Admin Panel")
}