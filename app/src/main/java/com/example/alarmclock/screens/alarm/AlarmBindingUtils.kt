package com.example.alarmclock.screens.alarm

import android.widget.TextView
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import com.example.alarmclock.R
import com.example.alarmclock.database.Alarm
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
        val (hours, minutes) = calcFromTimeMinutes(alarm.timeMinutes)
        text =
            String.format(resources.getString(R.string.time_hours_minutes_format), hours, minutes)
    }
}

@BindingAdapter("bind_alarmDays")
fun TextView.setAlarmDays(alarm: Alarm?) {
    alarm?.let {

        val weekdays = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        )
        val weekend = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val everyDay = weekdays + weekend

        text = when (alarm.days) {
            weekdays -> "Weekdays"
            weekend -> "Weekend"
            everyDay -> "Every Day"
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
