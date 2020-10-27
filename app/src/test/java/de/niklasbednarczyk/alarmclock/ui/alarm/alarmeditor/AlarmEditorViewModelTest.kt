package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import de.niklasbednarczyk.alarmclock.FakeAlarmDao
import de.niklasbednarczyk.alarmclock.MainCoroutineRule
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.getOrAwaitValue
import de.niklasbednarczyk.alarmclock.utils.WEEKDAYS
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
import java.time.DayOfWeek

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class AlarmEditorViewModelTest {

    private lateinit var alarmEditorViewModel: AlarmEditorViewModel

    private lateinit var alarmDao: AlarmDao

    private lateinit var alarm: Alarm
    private lateinit var defaultAlarm: Alarm

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
            days = WEEKDAYS.toMutableList(),
            snoozeLengthMinutes = 5,
            vibrationType = VibrationType.NORMAL,
            soundUri = "Sound".toUri()
        )
        defaultAlarm = Alarm(
            snoozeLengthMinutes = 10,
            vibrationType = VibrationType.FAST,
            soundUri = "Uri".toUri()
        )
        alarm.alarmId = alarmDao.insertAlarm(alarm)
        alarmEditorViewModel = AlarmEditorViewModel(alarmDao, ioDispatcher)
        alarmEditorViewModel.init(alarm.alarmId, defaultAlarm)
        alarmEditorViewModel.alarm.getOrAwaitValue { }
    }

    @Test
    fun onActionSave_shouldSetAlarmToActive() = mainCoroutineRule.runBlockingTest {
        // GIVEN

        // WHEN
        alarmEditorViewModel.onActionSave()
        val newAlarm = alarmDao.getAlarm(alarm.alarmId).getOrAwaitValue { }

        // THEN
        assertThat(newAlarm.isActive, `is`(true))
    }

    @Test
    fun alarmEditorListener_onDayButtonClick_shouldAddDay() {
        // GIVEN
        val actual = DayOfWeek.SATURDAY

        // WHEN
        alarmEditorViewModel.alarmEditorListener.onDayButtonClick(actual)

        // THEN
        assertThat(alarm.days.contains(actual), `is`(true))
    }

    @Test
    fun alarmEditorListener_onDayButtonClick_shouldRemoveDay() {
        // GIVEN
        val actual = DayOfWeek.MONDAY

        // WHEN
        alarmEditorViewModel.alarmEditorListener.onDayButtonClick(actual)

        // THEN
        assertThat(alarm.days.contains(actual), `is`(false))
    }

    @Test
    fun alarmEditorListener_onNameDialogPositiveButton_shouldChangeName() {
        // GIVEN
        val actual = "Name"

        // WHEN
        alarmEditorViewModel.alarmEditorListener.onNameDialogPositiveButton(actual)

        // THEN
        assertThat(alarm.name, `is`(actual))
    }


    @Test
    fun alarmEditorListener_onRingtonePickerSaved_shouldChangeSoundUri() {
        // GIVEN
        val actual = "Ringtone".toUri()

        // WHEN
        alarmEditorViewModel.alarmEditorListener.onRingtonePickerSaved(actual)

        // THEN
        assertThat(alarm.soundUri, `is`(actual))
    }

    @Test
    fun alarmEditorListener_onTimeDialogTimeSet_shouldChangeTimeMinutes() {
        // GIVEN
        val actualHour = 5
        val actualMinute = 5

        // WHEN
        alarmEditorViewModel.alarmEditorListener.onTimeDialogTimeSet(actualHour, actualMinute)

        // THEN
        assertThat(alarm.timeMinutes, `is`(actualHour.times(60) + actualMinute))
    }

    @Test
    fun alarmEditorListener_onSnoozeLengthDialogPositiveButton_shouldChangeSnoozeLength() {
        // GIVEN
        val actual = 12

        // WHEN
        alarmEditorViewModel.alarmEditorListener.onSnoozeLengthDialogPositiveButton(actual)

        // THEN
        assertThat(alarm.snoozeLengthMinutes, `is`(actual))
    }

}