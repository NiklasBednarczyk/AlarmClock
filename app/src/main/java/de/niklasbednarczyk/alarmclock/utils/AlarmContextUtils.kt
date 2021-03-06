package de.niklasbednarczyk.alarmclock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.preference.PreferenceManager
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.activities.MainActivity
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.enums.AlarmType
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.receivers.AlarmReceiver
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
    setAlarm(context, alarm, calendar, AlarmType.NORMAL)
}

fun setPreviewAlarm(context: Context?, alarm: Alarm) {
    val calendar = Calendar.getInstance()
    setAlarm(context, alarm, calendar, AlarmType.PREVIEW)
}

fun snoozeAlarm(
    context: Context?,
    alarm: Alarm,
    snoozeCount: Int,
    alarmType: AlarmType
) {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.MINUTE, alarm.snoozeLengthMinutes)
    }
    setAlarm(context, alarm, calendar, alarmType, snoozeCount)
}

private fun setAlarm(
    context: Context?,
    alarm: Alarm,
    calendar: Calendar,
    alarmType: AlarmType,
    snoozeCount: Int = 0
) {
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntentReceiver =
        createPendingIntentReceiver(context, alarm, alarmType, snoozeCount)
    val pendingIntentActivity = createPendingIntentActivity(context, alarm, alarmType)
    val alarmClockInfo =
        AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntentActivity)
    alarmManager.setAlarmClock(alarmClockInfo, pendingIntentReceiver)
}

fun cancelAlarm(context: Context?, alarm: Alarm, alarmType: AlarmType) {
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = createPendingIntentReceiver(context, alarm, alarmType)
    alarmManager.cancel(pendingIntent)
}

private fun createPendingIntentReceiver(
    context: Context?,
    alarm: Alarm,
    alarmType: AlarmType,
    snoozeCount: Int = 0,
): PendingIntent {
    val alarmId = getAlarmIdIntFromAlarmType(alarm, alarmType)
    return Intent(context, AlarmReceiver::class.java).apply {
        putExtra(AlarmReceiver.ALARM_INTENT_KEY_ALARM_ID, alarm.alarmId)
        putExtra(AlarmReceiver.ALARM_INTENT_KEY_SNOOZE_COUNT, snoozeCount)
        putExtra(AlarmReceiver.ALARM_INTENT_KEY_ALARM_TYPE, alarmType.toString())
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
    alarmType: AlarmType
): PendingIntent {
    val alarmId = getAlarmIdIntFromAlarmType(alarm, alarmType)
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

private fun getAlarmIdIntFromAlarmType(alarm: Alarm, alarmType: AlarmType): Int =
    if (alarmType == AlarmType.NORMAL) alarm.alarmId.toInt() else 0

fun getDefaultAlarm(context: Context): Alarm {
    val alarmSnoozeLengthKey =
        context.resources.getString(R.string.preference_key_alarm_snooze_length)
    val alarmVibrationTypeKey =
        context.resources.getString(R.string.preference_key_alarm_vibration_type)

    val alarmSnoozeLengthDefaultValue =
        context.resources.getString(R.string.preference_default_value_alarm_snooze_length)
    val alarmVibrationTypeDefaultValue =
        context.resources.getString(R.string.preference_default_value_alarm_vibration_type)

    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val defaultSnoozeLengthMinutesString =
        (sharedPreferences.getString(alarmSnoozeLengthKey, alarmSnoozeLengthDefaultValue))
            ?: alarmSnoozeLengthDefaultValue
    val vibrationTypeString =
        (sharedPreferences.getString(alarmVibrationTypeKey, alarmVibrationTypeDefaultValue)
            ?: alarmVibrationTypeDefaultValue)

    val defaultSnoozeLengthMinutes = defaultSnoozeLengthMinutesString.toInt()
    val defaultVibrationType =
        enumValueOf<VibrationType>(vibrationTypeString)

    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    return Alarm(
        snoozeLengthMinutes = defaultSnoozeLengthMinutes,
        vibrationType = defaultVibrationType,
        soundUri = defaultSoundUri
    )
}