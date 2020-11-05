package de.niklasbednarczyk.alarmclock.enums

enum class VibrationType(val pattern: LongArray) {
    NONE(longArrayOf(0)),
    SLOW(longArrayOf(0, 750, 750)),
    NORMAL(longArrayOf(0, 500, 500)),
    FAST(longArrayOf(0, 250, 250))
}