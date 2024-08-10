package model.countdownTimer

enum class TimerState(val value: String) {
    running("running"),
    paused("paused"),
    completed("completed"),
    notRunning("notRunning")
}