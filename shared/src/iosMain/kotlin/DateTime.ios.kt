
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import platform.Foundation.*

@Serializable
actual object DateTime {
    actual fun getFormattedDate(
        timestamp: String,
        inputFormat: String,
        outputFormat: String
    ): String {
        val df = NSDateFormatter().apply {
            dateFormat = inputFormat
            timeZone = NSTimeZone.timeZoneWithAbbreviation("GMT")!!
        }

        val date = df.dateFromString(timestamp)
        df.timeZone = NSTimeZone.localTimeZone
        df.dateFormat = outputFormat

        return df.stringFromDate(date!!)
    }

    actual fun formatTimeStamp(timeStamp: Long, outputFormat: String): String {
        val formatter = NSDateFormatter().apply {
            dateFormat = outputFormat
            timeZone = NSTimeZone.localTimeZone
        }
        val date = NSDate(timeStamp.toDouble() / 1000)
        return formatter.stringFromDate(date)
    }

    actual fun getDateInMilliSeconds(timeStamp: String, inputFormat: String): Long {
        if (timeStamp.trim().isEmpty()) return getCurrentTimeInMilliSeconds()

        val df = NSDateFormatter().apply {
            dateFormat = inputFormat
        }
        val date = df.dateFromString(timeStamp)
        return (date!!.timeIntervalSince1970 * 1000).toLong()
    }

    actual fun getCurrentTimeInMilliSeconds(): Long {
        return (NSDate().timeIntervalSince1970 * 1000).toLong()
    }

    actual fun getForwardedDate(
        forwardedDaya: Int,
        forwardedMonth: Int,
        outputFormat: String
    ): String {
        val calendar = NSCalendar.currentCalendar
        val currentDate = NSDate()
        val components = NSDateComponents().apply {
            day = forwardedDaya.toLong()
            month = forwardedMonth.toLong()
        }

        val forwardDate = calendar.dateByAddingComponents(components, currentDate, NSCalendarUnitDay)
        val dateFormatter = NSDateFormatter().apply {
            dateFormat = outputFormat
        }

        return dateFormatter.stringFromDate(forwardDate ?: currentDate)
    }

    actual fun getTimestampInMillisFromString(
        timestamp: String,
        inputFormat: String
    ): Long {
        TODO("Not yet implemented")
    }
}


/*
extension DateTime {
    actual static func getTimestampInMillisFromString(timestamp: String, inputFormat: String) -> Int64 {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = inputFormat
        dateFormatter.timeZone = TimeZone(abbreviation: "GMT") // Use GMT for consistency

        if let date = dateFormatter.date(from: timestamp) {
            return Int64(date.timeIntervalSince1970 * 1000) // Convert seconds to milliseconds
        } else {
            return 0 // Or handle parsing errors differently
        }
    }
}
 */