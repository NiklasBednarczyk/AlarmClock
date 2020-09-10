package com.example.alarmclock.values

import java.time.DayOfWeek

const val ALARM_INTENT_KEY_ALARM_ID = "alarm_id"

val WEEKDAYS = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY
)
val WEEKEND = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
val EVERY_DAY = WEEKDAYS + WEEKEND