package de.niklasbednarczyk.alarmclock.activities

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.niklasbednarczyk.alarmclock.DataBindingIdlingResource
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.di.AlarmDaoModule
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.getOrAwaitValue
import de.niklasbednarczyk.alarmclock.monitorActivity
import de.niklasbednarczyk.alarmclock.utils.WEEKDAYS
import de.niklasbednarczyk.alarmclock.utils.timeMinutesToHoursAndMinutes
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.DayOfWeek
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(AlarmDaoModule::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var alarmDao: AlarmDao

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun fabButton_openEditorWithDefaultAlarm() {
        // GIVEN
        val defaultAlarm = createEmptyAlarm()

        val activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN
        onView(withId(R.id.alarm_list_fab_add_alarm)).perform(click())

        // THEN
        checkAlarmInAlarmEditor(defaultAlarm)

        activityScenario.close()
    }

    @Test
    fun fabButton_openEditorAndSaveDefaultAlarm() {
        // GIVEN
        val defaultAlarm = createEmptyAlarm().apply {
            isActive = true
            alarmId = 1
        }

        val activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN
        onView(withId(R.id.alarm_list_fab_add_alarm)).perform(click())
        onView(withId(R.id.action_save)).perform(click())
        val alarms = alarmDao.getAllAlarms().getOrAwaitValue { }

        // THEN
        assertThat(alarms.size, `is`(1))
        assertThat(alarms[0], `is`(defaultAlarm))
        onView(withText(R.string.app_name)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun alarmCardView_openEditorWithClickedAlarm() {
        // GIVEN
        val alarm = createEmptyAlarm().apply {
            name = "Test Alarm Text"
            timeMinutes = 630
            days = WEEKDAYS.toMutableList()
            vibrationType = VibrationType.FAST
            snoozeLengthMinutes = 10
        }
        alarm.alarmId = alarmDao.insertAlarm(alarm)

        val activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN
        onView(withId(R.id.alarm_list_item_card_view)).perform(click())

        // THEN
        checkAlarmInAlarmEditor(alarm)

        activityScenario.close()
    }

    @Test
    fun alarmPopUpMenuEdit_openEditorWithClickedAlarm() {
        // GIVEN
        val alarm = createEmptyAlarm().apply {
            name = "Test Alarm Text"
            timeMinutes = 630
            days = WEEKDAYS.toMutableList()
            vibrationType = VibrationType.FAST
            snoozeLengthMinutes = 10
        }
        alarm.alarmId = alarmDao.insertAlarm(alarm)

        val activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN
        onView(withId(R.id.alarm_list_item_pop_up_menu)).perform(click())
        onView(withText(R.string.edit)).inRoot(RootMatchers.isPlatformPopup()).perform(click())

        // THEN
        checkAlarmInAlarmEditor(alarm)

        activityScenario.close()
    }

    @Test
    fun settingsActionBar_opensSettings() {
        // GIVEN
        val activityScenario = launchActivity()
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // WHEN
        onView(withId(R.id.action_settings)).perform(click())

        // THEN
        onView(withText(R.string.navigation_main_settings_fragment_label)).check(matches(isDisplayed()))

        activityScenario.close()
    }


    private fun launchActivity(): ActivityScenario<MainActivity> {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.alarm_list_recycler_view) as RecyclerView).itemAnimator =
                null
        }
        return activityScenario
    }

    private fun createEmptyAlarm(): Alarm = Alarm(
        snoozeLengthMinutes = 5,
        vibrationType = VibrationType.NORMAL,
        soundUri = "content://settings/system/alarm_alert".toUri(),
    )

    private fun checkAlarmInAlarmEditor(alarm: Alarm) {
        // Title
        onView(withText(R.string.set_alarm)).check(matches(isDisplayed()))

        // Time
        val (hours, minutes) = timeMinutesToHoursAndMinutes(alarm.timeMinutes)
        val timeText = String.format("%02d:%02d", hours, minutes)

        onView(withId(R.id.alarm_editor_time)).check(matches(withText(timeText)))

        // Toggle Buttons
        onView(withId(R.id.alarm_editor_day_monday)).check(
            matches(
                dayMatcher(
                    alarm,
                    DayOfWeek.MONDAY
                )
            )
        )
        onView(withId(R.id.alarm_editor_day_tuesday)).check(
            matches(
                dayMatcher(
                    alarm,
                    DayOfWeek.TUESDAY
                )
            )
        )
        onView(withId(R.id.alarm_editor_day_wednesday)).check(
            matches(
                dayMatcher(
                    alarm,
                    DayOfWeek.WEDNESDAY
                )
            )
        )
        onView(withId(R.id.alarm_editor_day_thursday)).check(
            matches(
                dayMatcher(
                    alarm,
                    DayOfWeek.THURSDAY
                )
            )
        )
        onView(withId(R.id.alarm_editor_day_friday)).check(
            matches(
                dayMatcher(
                    alarm,
                    DayOfWeek.FRIDAY
                )
            )
        )
        onView(withId(R.id.alarm_editor_day_saturday)).check(
            matches(
                dayMatcher(
                    alarm,
                    DayOfWeek.SATURDAY
                )
            )
        )
        onView(withId(R.id.alarm_editor_day_sunday)).check(
            matches(
                dayMatcher(
                    alarm,
                    DayOfWeek.SUNDAY
                )
            )
        )

        // Properties
        onView(
            allOf(
                withId(R.id.alarm_editor_property_value),
                isDescendantOfA(withId(R.id.alarm_editor_name))
            )
        ).check(matches(withText(alarm.name)))
        onView(
            allOf(
                withId(R.id.alarm_editor_property_value),
                isDescendantOfA(withId(R.id.alarm_editor_vibration_type))
            )
        ).check(matches(withText(getVibrationTypeText(alarm))))
        onView(
            allOf(
                withId(R.id.alarm_editor_property_value),
                isDescendantOfA(withId(R.id.alarm_editor_snooze_length))
            )
        ).check(matches(withText(getSnoozeLengthText(alarm))))
    }

    private fun dayMatcher(alarm: Alarm, dayOfWeek: DayOfWeek): Matcher<View> =
        if (alarm.days.contains(dayOfWeek)) {
            isChecked()
        } else {
            isNotChecked()
        }

    private fun getVibrationTypeText(alarm: Alarm): Int =
        when (alarm.vibrationType) {
            VibrationType.NONE -> R.string.app_vibration_type_none
            VibrationType.SLOW -> R.string.app_vibration_type_slow
            VibrationType.NORMAL -> R.string.app_vibration_type_normal
            VibrationType.FAST -> R.string.app_vibration_type_fast
        }

    private fun getSnoozeLengthText(alarm: Alarm): String =
        if (alarm.snoozeLengthMinutes == 1) {
            "${alarm.snoozeLengthMinutes} minute"
        } else {
            "${alarm.snoozeLengthMinutes} minutes"
        }


}