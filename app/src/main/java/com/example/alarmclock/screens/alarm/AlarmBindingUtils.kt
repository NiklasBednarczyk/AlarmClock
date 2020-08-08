package com.example.alarmclock.screens.alarm

import android.widget.TextView
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

@BindingAdapter("alarmTime")
fun TextView.setAlarmTime(alarm: Alarm?) {
    alarm?.let {
        val hours = alarm.timeMinutes.div(60)
        val minutes = alarm.timeMinutes.rem(60)
        text =
            String.format(resources.getString(R.string.time_hours_minutes_format), hours, minutes)
    }
}

@BindingAdapter("alarmDays")
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

@BindingAdapter("alarmName")
fun TextView.setAlarmName(alarm: Alarm?) {
    alarm?.let {
        text = alarm.name
    }
}
