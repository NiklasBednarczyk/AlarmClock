package de.niklasbednarczyk.alarmclock.ui.alarm.alarmlist

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
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class AlarmListViewModelTest {

    private lateinit var alarmListViewModel: AlarmListViewModel

    private lateinit var alarmDao: AlarmDao

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        alarmDao = FakeAlarmDao()
        val ioDispatcher = Dispatchers.Main
        alarmListViewModel = AlarmListViewModel(alarmDao, ioDispatcher)
    }

    @Test
    fun insertAlarm_shouldInsertAlarmWithNewIdAndSetNormalAlarm() {
        // GIVEN
        val alarm = createEmptyAlarm()

        // WHEN
        alarmListViewModel.insertAlarm(alarm)

        // THEN
        assertThat(alarmDao.getAlarm(alarm.alarmId).getOrAwaitValue { }, `is`(alarm))
        assertThat(alarmListViewModel.eventSetNormalAlarm.value, `is`(alarm))
    }

    @Test
    fun alarmOnItemClickListenerOnActiveClick_activeAlarm_shouldChangeIsActiveAndCancelAlarm() {
        // GIVEN
        val oldAlarm = createEmptyAlarm().apply { isActive = true }
        oldAlarm.alarmId = alarmDao.insertAlarm(oldAlarm)

        // WHEN
        alarmListViewModel.alarmOnItemClickListener.onActiveClick(oldAlarm)
        val newAlarm = alarmDao.getAlarm(oldAlarm.alarmId).getOrAwaitValue { }
        val eventCancelAlarm = alarmListViewModel.eventCancelAlarm.value

        // THEN
        assertThat(newAlarm.isActive, `is`(false))
        assertThat(eventCancelAlarm, `is`(newAlarm))
    }

    @Test
    fun alarmOnItemClickListenerOnActiveClick_notActiveAlarm_shouldChangeIsActiveAndStartAlarm() {
        // GIVEN
        val oldAlarm = createEmptyAlarm().apply { isActive = false }
        oldAlarm.alarmId = alarmDao.insertAlarm(oldAlarm)

        // WHEN
        alarmListViewModel.alarmOnItemClickListener.onActiveClick(oldAlarm)
        val newAlarm = alarmDao.getAlarm(oldAlarm.alarmId).getOrAwaitValue { }
        val eventSetNormalAlarm = alarmListViewModel.eventSetNormalAlarm.value

        // THEN
        assertThat(newAlarm.isActive, `is`(true))
        assertThat(eventSetNormalAlarm, `is`(newAlarm))
    }

    @Test
    fun alarmOnItemClickListenerOnCardViewClick_shouldStartNavigatingToAlarmEditor() {
        // GIVEN
        val alarmId = Long.MAX_VALUE

        // WHEN
        alarmListViewModel.alarmOnItemClickListener.onCardViewClick(alarmId)
        val navigateToAlarmEditor = alarmListViewModel.navigateToAlarmEditor.value

        // THEN
        assertThat(navigateToAlarmEditor, `is`(alarmId))
    }

    private fun createEmptyAlarm(): Alarm = Alarm(
        snoozeLengthMinutes = 5,
        vibrationType = VibrationType.NONE,
        soundUri = "Sound".toUri()
    )

}