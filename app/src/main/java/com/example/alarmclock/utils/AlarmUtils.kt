package com.example.alarmclock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmclock.activities.MainActivity
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.receivers.AlarmReceiver
import com.example.alarmclock.values.ALARM_INTENT_KEY_ALARM_ID
import java.time.DayOfWeek
import java.util.*

fun hoursAndMinutesToTimeMinutes(hours: Int, minutes: Int): Int = hours.times(60).plus(minutes)

fun timeMinutesToHoursAndMinutes(timeMinutes: Int): Pair<Int, Int> {
    val hours = timeMinutes.div(60)
    val minutes = timeMinutes.rem(60)
    return Pair(hours, minutes)
}

fun setAlarm(context: Context?, alarm: Alarm) {
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntentReceiver = createPendingIntentReceiver(context, alarm)
    val day = timeMinutesAndDaysToNextDay(alarm.timeMinutes, alarm.days)
    val (hours, minutes) = timeMinutesToHoursAndMinutes(alarm.timeMinutes)
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        val calendarTimeMinutes =
            hoursAndMinutesToTimeMinutes(get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE))
        if (get(Calendar.DAY_OF_WEEK) > day || get(Calendar.DAY_OF_WEEK) == day && calendarTimeMinutes == alarm.timeMinutes) {
            add(Calendar.DAY_OF_MONTH, 7)
        }
        set(Calendar.DAY_OF_WEEK, day)
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, 0)
    }
    val pendingIntentActivity = createPendingIntentActivity(context, alarm)
    val alarmClockInfo =
        AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntentActivity)

    alarmManager.setAlarmClock(alarmClockInfo, pendingIntentReceiver)
}

fun cancelAlarm(context: Context?, alarm: Alarm) {
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = createPendingIntentReceiver(context, alarm)
    alarmManager.cancel(pendingIntent)
}

private fun createPendingIntentReceiver(context: Context?, alarm: Alarm): PendingIntent =
    Intent(context, AlarmReceiver::class.java).apply {
        putExtra(ALARM_INTENT_KEY_ALARM_ID, alarm.alarmId)
    }.let { intent ->
        PendingIntent.getBroadcast(
            context,
            alarm.alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


private fun createPendingIntentActivity(context: Context?, alarm: Alarm): PendingIntent =
    Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }.let { intent ->
        PendingIntent.getActivity(
            context,
            alarm.alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

private fun timeMinutesAndDaysToNextDay(timeMinutes: Int, days: MutableList<DayOfWeek>): Int {
    val calendar = Calendar.getInstance()
    val calendarDayOfWeek = calendarDayToDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
    val calendarTimeMinutes = hoursAndMinutesToTimeMinutes(
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )

    var dayOfWeek = calendarDayOfWeek
    if (days.isNotEmpty()) {
        while (!days.contains(dayOfWeek)) {
            dayOfWeek = dayOfWeek.plus(1)
        }
        if (calendarTimeMinutes >= timeMinutes) {
            dayOfWeek = dayOfWeek.plus(1)
            while (!days.contains(dayOfWeek)) {
                dayOfWeek = dayOfWeek.plus(1)
            }
        }
    } else {
        if (calendarTimeMinutes >= timeMinutes) {
            dayOfWeek = dayOfWeek.plus(1)
        }
    }

    return dayOfWeekToCalendarDay(dayOfWeek)
}

private fun dayOfWeekToCalendarDay(dayOfWeek: DayOfWeek): Int = (dayOfWeek.value % 7) + 1

private fun calendarDayToDayOfWeek(calendarDay: Int): DayOfWeek {
    val t = (calendarDay + 6) % 7
    return if (t == 0) {
        DayOfWeek.SUNDAY
    } else {
        DayOfWeek.of(t)
    }
}