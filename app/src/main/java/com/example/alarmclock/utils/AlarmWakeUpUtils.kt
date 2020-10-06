package com.example.alarmclock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmclock.activities.MainActivity
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.receivers.AlarmReceiver
import java.util.*

fun setNormalAlarm(context: Context?, alarm: Alarm) {
    val day = timeMinutesAndDaysToNextDay(alarm.timeMinutes, alarm.days)
    val (hours, minutes) = timeMinutesToHoursAndMinutes(alarm.timeMinutes)
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        val calendarTimeMinutes = calendarToCalendarTimeMinutes(this)
        if (get(Calendar.DAY_OF_WEEK) > day || get(Calendar.DAY_OF_WEEK) == day && calendarTimeMinutes == alarm.timeMinutes) {
            add(Calendar.DAY_OF_MONTH, 7)
        }
        set(Calendar.DAY_OF_WEEK, day)
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, 0)
    }
    setAlarm(context, alarm, calendar, true)
}

fun setPreviewAlarm(context: Context?, alarm: Alarm) {
    val calendar = Calendar.getInstance()
    setAlarm(context, alarm, calendar, false)
}

fun snoozeAlarm(context: Context?, alarm: Alarm, snoozeCount: Int) {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.MINUTE, alarm.snoozeLengthMinutes)
    }
    setAlarm(context, alarm, calendar, true, snoozeCount)
}

private fun setAlarm(
    context: Context?,
    alarm: Alarm,
    calendar: Calendar,
    isNormalAlarm: Boolean,
    snoozeCount: Int = 0
) {
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntentReceiver =
        createPendingIntentReceiver(context, alarm, isNormalAlarm, snoozeCount)
    val pendingIntentActivity = createPendingIntentActivity(context, alarm, isNormalAlarm)
    val alarmClockInfo =
        AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntentActivity)
    alarmManager.setAlarmClock(alarmClockInfo, pendingIntentReceiver)
}

fun cancelAlarm(context: Context?, alarm: Alarm) {
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = createPendingIntentReceiver(context, alarm, true)
    alarmManager.cancel(pendingIntent)
}

private fun createPendingIntentReceiver(
    context: Context?,
    alarm: Alarm,
    isNormalAlarm: Boolean,
    snoozeCount: Int = 0,
): PendingIntent {
    val alarmId = if (isNormalAlarm) alarm.alarmId.toInt() else 0
    return Intent(context, AlarmReceiver::class.java).apply {
        putExtra(AlarmReceiver.ALARM_INTENT_KEY_ALARM_ID, alarm.alarmId)
        putExtra(AlarmReceiver.ALARM_INTENT_KEY_SNOOZE_COUNT, snoozeCount)
    }.let { intent ->
        PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

private fun createPendingIntentActivity(
    context: Context?,
    alarm: Alarm,
    isNormalAlarm: Boolean
): PendingIntent {
    val alarmId = if (isNormalAlarm) alarm.alarmId.toInt() else 0
    return Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }.let { intent ->
        PendingIntent.getActivity(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

