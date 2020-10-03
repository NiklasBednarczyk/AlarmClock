package com.example.alarmclock.screens.alarm.alarmwakeupview

enum class AlarmWakeUpViewVibrationType(val pattern: LongArray) {
    VIBRATION(longArrayOf(0, 100)),
    NO_VIBRATION(longArrayOf(0))
}