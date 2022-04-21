package das.omegaterapia.visits.model

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


/**
 * Type converter for ROOM database
 *
 * These methods convert the given type to a compatible type that SQLite supports and vice versa.
 * ROOM database automatically knows which type converters must use.
 */

class Converters {
    //--------   ZonedDateTime Converters   --------//

    // They convert from ZonedDateTime to long format and backwards. Time zone value is kept.

    @TypeConverter
    fun fromTimestamp(value: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault())
    }

    @TypeConverter
    fun dateToTimestamp(date: ZonedDateTime): Long {
        return date.toEpochSecond()
    }


    //---------   String List Converters   ---------//

    // They convert a list of string to a json and then parse that json back to a list.
    //  - It uses Kotlin serialization library.

    @TypeConverter
    fun listToJson(value: List<String>?): String = Json.encodeToString(value)

    @TypeConverter
    fun jsonToList(value: String): List<String> = Json.decodeFromString(value)
}
