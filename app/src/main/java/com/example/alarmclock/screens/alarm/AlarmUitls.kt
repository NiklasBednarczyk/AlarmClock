package com.example.alarmclock.screens.alarm

fun calcTimeMinutes(hours: Int, minutes: Int): Int = hours.times(60).plus(minutes)

fun calcFromTimeMinutes(timeMinutes: Int): Pair<Int, Int> {
    val hours = timeMinutes.div(60)
    val minutes = timeMinutes.rem(60)
    return Pair(hours, minutes)
}
