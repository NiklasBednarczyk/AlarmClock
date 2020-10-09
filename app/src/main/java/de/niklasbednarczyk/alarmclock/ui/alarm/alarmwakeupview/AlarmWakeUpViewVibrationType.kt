package de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview

enum class AlarmWakeUpViewVibrationType(val pattern: LongArray) {
    VIBRATION(longArrayOf(0, 100)),
    NO_VIBRATION(longArrayOf(0))
}