package com.example.alarmclock.utils

import android.text.format.DateFormat
import android.text.format.DateUtils
import android.widget.TextView
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import com.example.alarmclock.R
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.values.EVERY_DAY
import com.example.alarmclock.values.WEEKDAYS
import com.example.alarmclock.values.WEEKEND
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

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
                day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }
        }
    }
}

@BindingAdapter("bind_alarmName")
fun TextView.setAlarmName(alarm: Alarm?) {
    alarm?.let {
        text = alarm.name
    }
}

@BindingAdapter("bind_alarmDayAlarm", "bind_alarmDayDayOfWeek")
fun ToggleButton.setAlarmDay(alarm: Alarm?, dayOfWeek: DayOfWeek?) {
    dayOfWeek?.let {
        val dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
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