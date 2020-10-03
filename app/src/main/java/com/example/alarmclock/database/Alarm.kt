package com.example.alarmclock.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "alarm_table")
data class Alarm(
    @ColumnInfo(name = "alarm_id")
    @PrimaryKey(autoGenerate = true)
    var alarmId: Long = 0L,

    @ColumnInfo(name = "is_active")
    var isActive: Boolean,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "time_minutes")
    var timeMinutes: Int,

    @ColumnInfo(name = "days")
    var days: MutableList<DayOfWeek>,

    @ColumnInfo(name = "snooze_length_minutes")
    var snoozeLengthMinutes: Int,

    @ColumnInfo(name = "vibrate")
    var vibrate: Boolean
)