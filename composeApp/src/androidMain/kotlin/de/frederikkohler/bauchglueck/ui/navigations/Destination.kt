package de.frederikkohler.bauchglueck.ui.navigations

sealed class Destination(val route: String, val title: String) {
    data object Launch : Destination("Launch", "Launch")
    data object Login : Destination("Login", "Login")
    data object SignUp : Destination("Register", "Register")
    data object Home : Destination("Home", "Home")
    data object Calendar : Destination("Calendar", "Kalender")
    data object Timer : Destination("Timer", "Timer")
    data object Medication : Destination("Medication", "Medikation")
    data object Weight : Destination("Weight", "Gewicht")
    data object ShowAllWeights : Destination("ShowAllWeights", "Alle Gewichtungseinträge")
    data object AddWeight : Destination("AddWeight", "Gewicht hinzufügen")
    data object WaterIntake : Destination("WaterIntake", "Wasseraufnahme")
    data object AddTimer : Destination("AddTimer", "Timer hinzufügen")
    data object EditTimer : Destination("EditTimer", "Timer Bearbeiten")
}