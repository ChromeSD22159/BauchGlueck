package ui.navigations

import data.local.entitiy.CountdownTimer
import io.ktor.http.ContentType.Application.Json
import kotlinx.serialization.SerializationException

sealed class Destination(val route: String, val title: String) {
    data object Launch : Destination("Launch", "Launch")
    data object Login : Destination("Login", "Login")
    data object SignUp : Destination("Register", "Register")
    data object ForgotPassword : Destination("ForgotPassword", "ForgotPassword")
    data object Home : Destination("Home", "Home")
    data object Calendar : Destination("Calendar", "Kalender")
    data object Timer : Destination("Timer", "Timer")
    data object Medication : Destination("Medication", "Medikation")
    data object AddMedication : Destination("AddMedication", "Medikation hinzufügen")
    data object EditMedication : Destination("EditMedication", "Medikation Bearbeiten")
    data object Weight : Destination("Weight", "Gewicht")
    data object ShowAllWeights : Destination("ShowAllWeights", "Alle Gewichtungseinträge")
    data object AddWeight : Destination("AddWeight", "Gewicht hinzufügen")
    data object WaterIntake : Destination("WaterIntake", "Wasseraufnahme")
    data object AddTimer : Destination("AddTimer", "Timer hinzufügen")
    data object EditTimer : Destination("EditTimer", "Timer Bearbeiten")
    data object Recipes : Destination("Recipes", "Rezepte")
    data object Settings : Destination("Settings", "Settings")
}