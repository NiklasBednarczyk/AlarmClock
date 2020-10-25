package de.niklasbednarczyk.alarmclock.utils

import android.content.Context
import android.media.RingtoneManager
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmContextUtilsTest {

    lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun getDefaultAlarm_useSharedPreferences() {
        // GIVEN
        val alarmSnoozeLengthKey =
            context.resources.getString(R.string.preference_key_alarm_snooze_length)
        val alarmVibrationTypeKey =
            context.resources.getString(R.string.preference_key_alarm_vibration_type)

        val alarmSnoozeLengthValue = 10
        val alarmVibrationTypeValue = VibrationType.NONE

        // WHEN
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(alarmSnoozeLengthKey, alarmSnoozeLengthValue.toString())
            .apply()
        sharedPreferences.edit()
            .putString(alarmVibrationTypeKey, alarmVibrationTypeValue.toString()).apply()
        val defaultAlarm = getDefaultAlarm(context)

        // THEN
        assertThat(defaultAlarm.snoozeLengthMinutes, `is`(alarmSnoozeLengthValue))
        assertThat(defaultAlarm.vibrationType, `is`(alarmVibrationTypeValue))
    }

    @Test
    fun getDefaultAlarm_useDefaultUri() {
        // GIVEN
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        // WHEN
        val defaultAlarm = getDefaultAlarm(context)

        // THEN
        assertThat(defaultAlarm.soundUri, `is`(defaultUri))
    }

}