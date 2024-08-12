import android.os.Build
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Serializable
actual object DateTime {
    @JvmStatic
    actual fun getFormattedDate(
        timestamp: String,
        inputFormat: String,
        outputFormat: String
    ): String {
        val dateFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
        dateFormatter.timeZone = TimeZone.getTimeZone("GMT")

        val parser = SimpleDateFormat(inputFormat, Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("GMT")

        return try {
            val date = parser.parse(timestamp)
            if (date != null) {
                dateFormatter.timeZone = TimeZone.getDefault()
                dateFormatter.format(date)
            } else {
                timestamp
            }
        } catch (e: Exception) {
            e.printStackTrace()
            timestamp
        }
    }

    actual fun formatTimeStamp(timeStamp: Long, outputFormat: String): String {
        val sdf = SimpleDateFormat(outputFormat, Locale.getDefault())
        return sdf.format(Date(timeStamp * 1000))
    }

    actual fun getDateInMilliSeconds(timeStamp: String, inputFormat: String): Long {
        if (timeStamp.trim().isEmpty()) return getCurrentTimeInMilliSeconds()

        val parser = SimpleDateFormat(inputFormat, Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("GMT")
        return parser.parse(timeStamp)?.time ?: 0
    }

    actual fun getCurrentTimeInMilliSeconds(): Long {
        return System.currentTimeMillis()
    }

    actual fun getForwardedDate(
        forwardedDaya: Int,
        forwardedMonth: Int,
        outputFormat: String
    ): String {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, forwardedDaya)
            add(Calendar.MONTH, forwardedMonth)
        }

        val dateFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)
        return dateFormat.format(Date(calendar.timeInMillis))
    }

    actual fun getTimestampInMillisFromString(timestamp: String, inputFormat: String): Long {
        if (timestamp.trim().isEmpty()) return 0L // Or handle empty string differently

        val parser = SimpleDateFormat(inputFormat, Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("GMT")
        return try {
            parser.parse(timestamp)?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L // Or handle parsing errors differently
        }
    }
}