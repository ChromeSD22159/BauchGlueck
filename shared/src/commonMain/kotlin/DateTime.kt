import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
expect object DateTime {
    fun getFormattedDate(
        timestamp: String,
        inputFormat: String,
        outputFormat: String
    ): String

    fun formatTimeStamp(
        timeStamp: Long,
        outputFormat: String = "yyyy-MM-dd"
    ): String

    fun getDateInMilliSeconds(timeStamp: String, inputFormat: String): Long

    fun getCurrentTimeInMilliSeconds(): Long

    fun getForwardedDate(
        forwardedDaya: Int = 0,
        forwardedMonth: Int = 0,
        outputFormat: String = "yyyy_MM_dd_T_HH_mm_ss"
    ): String

    fun getTimestampInMillisFromString(
        timestamp: String,
        inputFormat: String
    ): Long
}