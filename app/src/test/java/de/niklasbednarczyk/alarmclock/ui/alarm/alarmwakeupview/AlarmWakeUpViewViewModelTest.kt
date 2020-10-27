package de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import de.niklasbednarczyk.alarmclock.FakeAlarmDao
import de.niklasbednarczyk.alarmclock.MainCoroutineRule
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AlarmWakeUpViewViewModelTest {

    private lateinit var alarmWakeUpViewViewModel: AlarmWakeUpViewViewModel

    private lateinit var alarmDao: AlarmDao

    private lateinit var alarm: Alarm

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        alarmDao = FakeAlarmDao()
        val ioDispatcher = Dispatchers.Main
        alarm = Alarm(
            isActive = true,
            snoozeLengthMinutes = 5,
            vibrationType = VibrationType.NORMAL,
            soundUri = "Sound".toUri()
        )
        alarm.alarmId = alarmDao.insertAlarm(alarm)
        alarmWakeUpViewViewModel = AlarmWakeUpViewViewModel(alarmDao, ioDispatcher)
        alarmWakeUpViewViewModel.init(alarm.alarmId, 1)
        alarmWakeUpViewViewModel.alarm.getOrAwaitValue { }
    }

    @Test
    fun dismissOneShotAlarm_shouldSetIsActiveToFalse() = mainCoroutineRule.runBlockingTest {
        // GIVEN

        // WHEN
        alarmWakeUpViewViewModel.dismissOneShotAlarm()
        val newAlarm = alarmDao.getAlarm(alarm.alarmId).getOrAwaitValue { }

        // THEN
        assertThat(newAlarm.isActive, `is`(false))
    }

}