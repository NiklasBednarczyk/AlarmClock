package de.niklasbednarczyk.alarmclock.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import java.time.DayOfWeek

class AlarmClockTypeConverter {

    companion object {
        private const val SEPARATOR = ","
    }

    @TypeConverter
    fun fromDays(daysOfWeek: MutableList<DayOfWeek>): String =
        daysOfWeek.map { it.value }.joinToString(separator = SEPARATOR)

    @TypeConverter
    fun toDays(daysOfWeek: String): MutableList<DayOfWeek> {
        return if (daysOfWeek != "") {
            daysOfWeek.split(SEPARATOR).map { DayOfWeek.of(it.toInt()) }.sorted()
                .toMutableList()
        } else {
            mutableListOf()
        }
    }

    @TypeConverter
    fun fromSound(uri: Uri): String = uri.toString()

    @TypeConverter
    fun toSound(uri: String): Uri = uri.toUri()

    @TypeConverter
    fun fromVibration(vibrationType: VibrationType): String = vibrationType.name

    @TypeConverter
    fun toVibration(vibrationType: String): VibrationType = enumValueOf(vibrationType)

}