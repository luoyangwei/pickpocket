import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 *
 * @author luoyangwei by 2022-08-11 16:26 created
 *
 */
object DateFormat {
    private var formatPattern = "yyyy-MM-dd HH:mm:ss"

    fun toDate(stringDate: String): LocalDateTime {
        val dateTimeFormatter = DateTimeFormatter.ofPattern(formatPattern)
        return LocalDateTime.parse(stringDate, dateTimeFormatter)
    }

    fun toDateByHour(stringDate: String): LocalDateTime {
        val localDateTime = getNowDate()
        localDateTime.format(DateTimeFormatter.ofPattern(formatPattern))
        return toDate("${localDateTime.year}-${repair(localDateTime.month.value)}-${repair(localDateTime.dayOfMonth)} $stringDate:00")
    }

    fun getNowDate(): LocalDateTime {
        return LocalDateTime.now()
    }

    fun getNowDateTimeStamp(): Long {
        return toTimestamp(getNowDate())
    }

    fun toTimestamp(localDateTime: LocalDateTime): Long {
        return localDateTime.toEpochSecond(ZoneOffset.of("+8"))
    }

    fun repair(i: Int): String {
        return if (i < 10) "0$i" else "$i"
    }

}