package de.niklasbednarczyk.alarmclock.utils

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

fun timeMinutesToTimeMilliseconds(timeMinutes: Int): Long =
    timeMinutes.times(1000).times(60).toLong()

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

fun timeMinutesAndDaysToNextDay(timeMinutes: Int, days: MutableList<DayOfWeek>): Int {
    val calendarNow = Calendar.getInstance()
    val calendarNowDayOfWeek = calendarToDayOfWeek(calendarNow)
    val calendarNowTimeMinutes = calendarToCalendarTimeMinutes(calendarNow)

    var dayOfWeek = calendarNowDayOfWeek
    if (days.isNotEmpty()) {
        while (!days.contains(dayOfWeek)) {
            dayOfWeek = dayOfWeek.plus(1)
        }
        if (dayOfWeek == calendarNowDayOfWeek && calendarNowTimeMinutes >= timeMinutes) {
            dayOfWeek = dayOfWeek.plus(1)
            while (!days.contains(dayOfWeek)) {
                dayOfWeek = dayOfWeek.plus(1)
            }
        }
    } else {
        if (calendarNowTimeMinutes >= timeMinutes) {
            dayOfWeek = dayOfWeek.plus(1)
        }
    }

    return dayOfWeekToCalendarDay(dayOfWeek)
}