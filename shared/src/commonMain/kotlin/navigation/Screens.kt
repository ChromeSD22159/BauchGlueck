package navigation

enum class Screens(val route: String,val title: String) {
    Launch("Launch", "Launch"),
    Login("Login", "Login"),
    SignUp("Register", "Register"),
    Home("Home", "Home"),
    Calendar("Calendar", "Kalender"),
    Timer("Timer", "Timer"),
    Weight("Weight", "Gewicht"),
    WaterIntake("WaterIntake", "Wasseraufnahme"),
    AddTimer("AddTimer", "Timer hinzufügen"),
}
