package de.niklasbednarczyk.alarmclock.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import java.time.DayOfWeek

class AlarmClockConverter {

    companion object {
        private const val SEPARATOR = ","
    }

    @TypeConverter
    fun daysOfWeekToString(daysOfWeek: MutableList<DayOfWeek>): String =
        daysOfWeek.map { it.value }.joinToString(separator = SEPARATOR)

    @TypeConverter
    fun stringToDaysOfWeek(daysOfWeek: String): MutableList<DayOfWeek> {
        return if (daysOfWeek != "") {
            daysOfWeek.split(SEPARATOR).map { DayOfWeek.of(it.toInt()) }.sorted()
                .toMutableList()
        } else {
            mutableListOf()
        }
    }

    @TypeConverter
    fun uriToString(uri: Uri): String = uri.toString()

    @TypeConverter
    fun stringToUri(string: String): Uri = string.toUri()

}