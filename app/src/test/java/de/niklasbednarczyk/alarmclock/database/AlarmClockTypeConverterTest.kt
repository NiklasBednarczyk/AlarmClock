package de.niklasbednarczyk.alarmclock.database

import androidx.core.net.toUri
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.utils.EVERY_DAY
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlarmClockTypeConverterTest {

    private val alarmClockTypeConverter = AlarmClockTypeConverter()

    @Test
    fun daysConverter() {
        // GIVEN
        val givenDays = EVERY_DAY.toMutableList()

        // WHEN
        val actualString = alarmClockTypeConverter.fromDays(givenDays)
        val actualDays = alarmClockTypeConverter.toDays(actualString)

        // THEN
        assertThat(givenDays, `is`(actualDays))
    }

    @Test
    fun soundConverter() {
        // GIVEN
        val givenSound = "SOME_URI".toUri()

        // WHEN
        val actualString = alarmClockTypeConverter.fromSound(givenSound)
        val actualSound = alarmClockTypeConverter.toSound(actualString)

        // THEN
        assertThat(givenSound, `is`(actualSound))
    }

    @Test
    fun vibrationConverter() {
        // GIVEN
        val givenVibration = VibrationType.NORMAL

        // WHEN
        val actualString = alarmClockTypeConverter.fromVibration(givenVibration)
        val actualVibration = alarmClockTypeConverter.toVibration(actualString)

        // THEN
        assertThat(givenVibration, `is`(actualVibration))
    }

}