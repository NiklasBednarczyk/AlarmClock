package de.niklasbednarczyk.alarmclock.database

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import java.time.DayOfWeek

@Entity(tableName = "alarm_table")
data class Alarm(
    @ColumnInfo(name = "alarm_id")
    @PrimaryKey(autoGenerate = true)
    var alarmId: Long = 0L,

    @ColumnInfo(name = "is_active")
    var isActive: Boolean = false,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "time_minutes")
    var timeMinutes: Int = 7.times(60),

    @ColumnInfo(name = "days")
    var days: MutableList<DayOfWeek> = mutableListOf(),

    @ColumnInfo(name = "snooze_length_minutes")
    var snoozeLengthMinutes: Int,

    @ColumnInfo(name = "vibration_type")
    var vibrationType: VibrationType,

    @ColumnInfo(name = "sound_uri")
    var soundUri: Uri

)