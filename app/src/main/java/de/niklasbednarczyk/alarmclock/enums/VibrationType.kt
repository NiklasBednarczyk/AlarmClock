package de.niklasbednarczyk.alarmclock.enums

enum class VibrationType(val pattern: LongArray) {
    VIBRATION(longArrayOf(0, 100)),
    NO_VIBRATION(longArrayOf(0))
}