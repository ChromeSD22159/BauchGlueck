package model.countdownTimer

import DateTime
import dev.gitlive.firebase.firestore.FieldValue
import kotlinx.serialization.Serializable

@Serializable
data class CountdownTimer(
    var id: String = "",
    var userId: String = "",
    var name: String = "",
    var duration: Int = 0,
    var startDate: FieldValue?,
    var endDate: FieldValue?,
    var timerState: String = "",
    var timerType: String = "",
    var remainingDuration: Int = 0,
    var notificate: Boolean = true,
    var showActivity: Boolean = true,
)