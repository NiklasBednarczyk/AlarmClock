package com.example.alarmclock.dataclasses

import java.time.DayOfWeek

data class Alarm(
    var alarmId: Long,
    var isActive: Boolean,
    var name: String,
    var timeMinutes: Int,
    var days: List<DayOfWeek>
)