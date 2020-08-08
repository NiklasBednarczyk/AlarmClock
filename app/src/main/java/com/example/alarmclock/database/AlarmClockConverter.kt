package com.example.alarmclock.database

import androidx.room.TypeConverter
import java.time.DayOfWeek

class AlarmClockConverter {

    companion object {
        private const val SEPARATOR = ","
    }

    @TypeConverter
    fun daysOfWeekToString(daysOfWeek: List<DayOfWeek>): String =
        daysOfWeek.map { it.value }.joinToString(separator = SEPARATOR)

    @TypeConverter
    fun stringToDaysOfWeek(daysOfWeek: String): List<DayOfWeek> =
        daysOfWeek.split(SEPARATOR).map { DayOfWeek.of(it.toInt()) }.toList().sorted()

}