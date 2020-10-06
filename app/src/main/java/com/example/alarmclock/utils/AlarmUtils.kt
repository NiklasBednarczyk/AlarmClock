package com.example.alarmclock.utils

import android.content.Context
import android.media.RingtoneManager
import androidx.preference.PreferenceManager
import com.example.alarmclock.database.Alarm
import java.time.DayOfWeek
import java.util.*

fun hoursAndMinutesToTimeMinutes(hours: Int, minutes: Int): Int = hours.times(60).plus(minutes)

fun timeMinutesToHoursAndMinutes(timeMinutes: Int): Pair<Int, Int> {
    val hours = timeMinutes.div(60)
    val minutes = timeMinutes.rem(60)
    return Pair(hours, minutes)
}

fun calendarToCalendarTimeMinutes(calendar: Calendar): Int =
    hoursAndMinutesToTimeMinutes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

fun calendarToCalendarHoursAndMinutes(calendar: Calendar): Pair<Int, Int> {
    val calendarTimeMinutes = calendarToCalendarTimeMinutes(calendar)
    return timeMinutesToHoursAndMinutes(calendarTimeMinutes)
}

fun snoozeLengthMinutesToTimeMilliseconds(snoozeLengthMinutes: Int): Long =
    snoozeLengthMinutes.times(1000).times(60).toLong()

fun timeMinutesAndDaysToNextDay(timeMinutes: Int, days: MutableList<DayOfWeek>): Int {
    val calendar = Calendar.getInstance()
    val calendarDayOfWeek = calendarToDayOfWeek(calendar)
    val calendarTimeMinutes = calendarToCalendarTimeMinutes(calendar)


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

fun dayOfWeekToCalendarDay(dayOfWeek: DayOfWeek): Int = (dayOfWeek.value % 7) + 1

fun calendarToDayOfWeek(calendar: Calendar): DayOfWeek {
    val calendarDay = calendar.get(Calendar.DAY_OF_WEEK)
    val t = (calendarDay + 6) % 7
    return if (t == 0) {
        DayOfWeek.SUNDAY
    } else {
        DayOfWeek.of(t)
    }
}

fun getDefaultAlarm(context: Context?): Alarm {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val defaultSnoozeLengthMinutes =
        (sharedPreferences.getString("alarm_snooze_length", "5")?.toInt()) ?: 5
    val defaultVibrate = sharedPreferences.getBoolean("alarm_vibrate", true)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    return Alarm(
        snoozeLengthMinutes = defaultSnoozeLengthMinutes,
        vibrate = defaultVibrate,
        soundUri = defaultSoundUri
    )
}