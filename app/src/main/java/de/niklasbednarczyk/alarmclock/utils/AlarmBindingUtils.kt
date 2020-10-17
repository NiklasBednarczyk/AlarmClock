package de.niklasbednarczyk.alarmclock.utils

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.enums.AlarmPropertyType
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

val WEEKDAYS = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY
)
val WEEKEND = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
val EVERY_DAY = WEEKDAYS + WEEKEND

@BindingAdapter("alarmIsActive")
fun com.google.android.material.switchmaterial.SwitchMaterial.setAlarmIsActive(alarm: Alarm?) {
    alarm?.let {
        isChecked = alarm.isActive
    }
}

@BindingAdapter("bind_alarmTime")
fun TextView.setAlarmTime(alarm: Alarm?) {
    alarm?.let {
        val (hours, minutes) = timeMinutesToHoursAndMinutes(alarm.timeMinutes)
        val timeFormatted =
            String.format(resources.getString(R.string.time_hours_minutes_format), hours, minutes)
        val storedDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = storedDateFormat.parse(timeFormatted)
        date?.let {
            val systemDateFormat = DateFormat.getTimeFormat(context)
            text = systemDateFormat.format(date)
        }
    }
}

@BindingAdapter("bind_alarmDays")
fun TextView.setAlarmDays(alarm: Alarm?) {
    alarm?.let {
        text = when (alarm.days) {
            WEEKDAYS -> resources.getString(R.string.weekdays)
            WEEKEND -> resources.getString(R.string.weekend)
            EVERY_DAY -> resources.getString(R.string.every_day)
            mutableListOf<DayOfWeek>() -> {
                val calendar = Calendar.getInstance()
                val calendarTimeMinutes = calendarToCalendarTimeMinutes(calendar)
                if (calendarTimeMinutes < alarm.timeMinutes) {
                    resources.getString(R.string.today)
                } else {
                    resources.getString(R.string.tomorrow)
                }
            }
            else -> alarm.days.joinToString(", ") { day ->
                day.getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
            }
        }
    }
}

@BindingAdapter("bind_alarmName")
fun TextView.setAlarmName(alarm: Alarm?) {
    alarm?.let {
        text = getTextForPropertyName(alarm)
    }
}

@BindingAdapter("bind_alarmDayAlarm", "bind_alarmDayDayOfWeek")
fun ToggleButton.setAlarmDay(alarm: Alarm?, dayOfWeek: DayOfWeek?) {
    dayOfWeek?.let {
        val dayOfWeekString =
            dayOfWeek.getDisplayName(TextStyle.NARROW_STANDALONE, Locale.getDefault())
        textOn = dayOfWeekString
        textOff = dayOfWeekString
        alarm?.let {
            isChecked = alarm.days.contains(dayOfWeek)
        }
    }
}

@BindingAdapter("bind_alarmSnoozeTime")
fun TextView.setSnoozeTime(snoozeTime: Long?) {
    snoozeTime?.let {
        text = DateUtils.formatElapsedTime(snoozeTime)
    }
}

@BindingAdapter("bind_alarmSnoozeCount")
fun TextView.setSnoozeCount(snoozeCount: Int?) {
    snoozeCount?.let {
        text = String.format(
            resources.getQuantityString(
                R.plurals.snooze_count,
                snoozeCount,
                snoozeCount
            )
        )
    }
}

@BindingAdapter("bind_alarmPropertyImageAlarm", "bind_alarmPropertyImageType")
fun ImageView.setPropertyImage(
    alarm: Alarm?,
    alarmPropertyType: AlarmPropertyType?,
) {
    alarm?.let {
        alarmPropertyType?.let {
            val drawableId = when (alarmPropertyType) {
                AlarmPropertyType.NAME -> R.drawable.ic_baseline_label_24
                AlarmPropertyType.SNOOZE_LENGTH -> R.drawable.ic_baseline_snooze_24
                AlarmPropertyType.SOUND -> R.drawable.ic_baseline_music_note_24
            }
            val drawable = ContextCompat.getDrawable(context, drawableId)
            setImageDrawable(drawable)

            contentDescription = when (alarmPropertyType) {
                AlarmPropertyType.NAME -> resources.getString(R.string.description_property_name)
                AlarmPropertyType.SNOOZE_LENGTH -> resources.getString(R.string.description_property_snooze_length)
                AlarmPropertyType.SOUND -> resources.getString(R.string.description_property_sound)
            }
        }
    }
}

@BindingAdapter("bind_alarmPropertyTitleAlarm", "bind_alarmPropertyTitleType")
fun TextView.setPropertyTitle(alarm: Alarm?, alarmPropertyType: AlarmPropertyType?) {
    alarm?.let {
        alarmPropertyType?.let {
            text = when (alarmPropertyType) {
                AlarmPropertyType.NAME -> resources.getString(R.string.alarm_editor_property_title_name)
                AlarmPropertyType.SNOOZE_LENGTH -> resources.getString(R.string.alarm_editor_property_title_snooze_length)
                AlarmPropertyType.SOUND -> resources.getString(R.string.alarm_editor_property_title_sound)
            }
        }
    }
}

@BindingAdapter("bind_alarmPropertyValueAlarm", "bind_alarmPropertyValueType")
fun TextView.setPropertyValue(alarm: Alarm?, alarmPropertyType: AlarmPropertyType?) {
    alarm?.let {
        alarmPropertyType?.let {
            text = when (alarmPropertyType) {
                AlarmPropertyType.NAME -> getTextForPropertyName(alarm)
                AlarmPropertyType.SNOOZE_LENGTH -> getTextForPropertySnoozeLength(alarm, context)
                AlarmPropertyType.SOUND -> getTextForPropertySound(alarm, context)
            }
        }
    }
}


private fun getTextForPropertyName(alarm: Alarm): String = alarm.name

private fun getTextForPropertySnoozeLength(alarm: Alarm, context: Context): String =
    String.format(
        context.resources.getQuantityString(
            R.plurals.snooze_length_minutes,
            alarm.snoozeLengthMinutes,
            alarm.snoozeLengthMinutes
        )
    )

private fun getTextForPropertySound(alarm: Alarm, context: Context): String {
    return if (Uri.EMPTY != alarm.soundUri) {
        val ringtone = RingtoneManager.getRingtone(context, alarm.soundUri)
        ringtone.getTitle(context)
    } else {
        context.resources.getString(R.string.no_sound)
    }
}