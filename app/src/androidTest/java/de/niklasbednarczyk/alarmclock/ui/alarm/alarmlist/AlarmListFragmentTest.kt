package de.niklasbednarczyk.alarmclock.ui.alarm.alarmlist

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.activities.MainActivity
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.di.AlarmDaoModule
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.getOrAwaitValue
import de.niklasbednarczyk.alarmclock.utils.EVERY_DAY
import de.niklasbednarczyk.alarmclock.utils.WEEKDAYS
import de.niklasbednarczyk.alarmclock.utils.WEEKEND
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNull.nullValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@UninstallModules(AlarmDaoModule::class)
@HiltAndroidTest
class AlarmListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var alarmDao: AlarmDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun alarmTimeMinutes_shouldShowTime() {
        // GIVEN
        val alarm = createEmptyAlarm().apply { timeMinutes = 360 }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()

        // THEN
        onView(withText("06:00")).check(matches(isDisplayed()))
    }

    @Test
    fun alarmDays_shouldShowStringWeekdays() {
        // GIVEN
        val alarm = createEmptyAlarm().apply { days = WEEKDAYS.toMutableList() }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()

        // THEN
        onView(withText(R.string.weekdays)).check(matches(isDisplayed()))
    }

    @Test
    fun alarmDays_shouldShowStringWeekend() {
        // GIVEN
        val alarm = createEmptyAlarm().apply { days = WEEKEND.toMutableList() }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()

        // THEN
        onView(withText(R.string.weekend)).check(matches(isDisplayed()))
    }

    @Test
    fun alarmDays_shouldShowStringEveryDay() {
        // GIVEN
        val alarm = createEmptyAlarm().apply { days = EVERY_DAY.toMutableList() }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()

        // THEN
        onView(withText(R.string.every_day)).check(matches(isDisplayed()))
    }

    @Test
    fun alarmName_shouldShowName() {
        // GIVEN
        val alarm = createEmptyAlarm().apply { name = "Test Alarm Name" }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()

        // THEN
        onView(withText(alarm.name)).check(matches(isDisplayed()))
    }

    @Test
    fun alarmIsActive_shouldShowFalse() {
        // GIVEN
        val alarm = createEmptyAlarm().apply {
            name = "Test Alarm Name"
            isActive = false
        }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()

        // THEN
        onView(switchWithText(alarm.name)).check(matches(isDisplayed()))
        onView(switchWithText(alarm.name)).check(matches(isNotChecked()))
    }

    @Test
    fun alarmIsActive_shouldShowTrue() {
        // GIVEN
        val alarm = createEmptyAlarm().apply {
            name = "Test Alarm Name"
            isActive = true
        }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()

        // THEN
        onView(switchWithText(alarm.name)).check(matches(isDisplayed()))
        onView(switchWithText(alarm.name)).check(matches(isChecked()))
    }

    @Test
    fun alarmIsActive_false_shouldSwitchOnClick() {
        // GIVEN
        val alarm = createEmptyAlarm().apply {
            name = "Test Alarm Name"
            isActive = false
        }
        alarm.alarmId = alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()
        onView(switchWithText(alarm.name)).perform(click())
        val alarmDb = alarmDao.getAlarm(alarm.alarmId).getOrAwaitValue { }

        // THEN
        onView(switchWithText(alarm.name)).check(matches(isChecked()))
        assertThat(alarmDb.isActive, `is`(true))
    }

    @Test
    fun alarmIsActive_true_shouldSwitchOnClick() {
        // GIVEN
        val alarm = createEmptyAlarm().apply {
            name = "Test Alarm Name"
            isActive = true
        }
        alarm.alarmId = alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()
        onView(switchWithText(alarm.name)).perform(click())
        val alarmDb = alarmDao.getAlarm(alarm.alarmId).getOrAwaitValue { }

        // THEN
        onView(switchWithText(alarm.name)).check(matches(isNotChecked()))
        assertThat(alarmDb.isActive, `is`(false))
    }

    @Test
    fun popUpMenu_shouldOpenOnClick() {
        // GIVEN
        val alarm = createEmptyAlarm()
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()
        onView(withId(R.id.alarm_list_item_pop_up_menu)).perform(click())

        // THEN
        onView(withText(R.string.edit)).inRoot(isPlatformPopup()).check(matches(isDisplayed()))
        onView(withText(R.string.duplicate)).inRoot(isPlatformPopup()).check(matches(isDisplayed()))
        onView(withText(R.string.preview)).inRoot(isPlatformPopup()).check(matches(isDisplayed()))
        onView(withText(R.string.delete)).inRoot(isPlatformPopup()).check(matches(isDisplayed()))
    }

    @Test
    fun popUpMenu_duplicate_shouldDuplicateAlarmOnClick() {
        // GIVEN
        val alarm = createEmptyAlarm().apply {
            isActive = false
            name = "This is a Test name"
            timeMinutes = 600
            days = WEEKDAYS.toMutableList()
            snoozeLengthMinutes = 12
            vibrationType = VibrationType.FAST
            soundUri = "TestSound".toUri()
        }
        alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()
        onView(withId(R.id.alarm_list_item_pop_up_menu)).perform(click())
        onView(withText(R.string.duplicate)).inRoot(isPlatformPopup()).perform(click())
        val alarms = alarmDao.getAllAlarms().getOrAwaitValue { }

        // THEN
        onView(allOf(withText(alarm.name))).check(matches(isDisplayed()))
        assertThat(alarms.size, `is`(2))
        assertThat(alarms[0], `is`(alarms[1]))
    }

    @Test
    fun popUpMenu_delete_withName_shouldDeleteAlarmOnClickAndShowSnackbar() {
        // GIVEN
        val alarm = createEmptyAlarm().apply { name = "Testing name" }
        alarm.alarmId = alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()
        onView(withId(R.id.alarm_list_item_pop_up_menu)).perform(click())
        onView(withText(R.string.delete)).inRoot(isPlatformPopup()).perform(click())
        val alarmDb = alarmDao.getAlarm(alarm.alarmId).getOrAwaitValue { }

        // THEN
        onView(withText("Removed: ${alarm.name}")).check(matches(isDisplayed()))
        assertThat(alarmDb, `is`(nullValue()))
    }

    @Test
    fun popUpMenu_delete_withoutName_shouldDeleteAlarmOnClickAndShowSnackbar() {
        // GIVEN
        val alarm = createEmptyAlarm().apply { name = "" }
        alarm.alarmId = alarmDao.insertAlarm(alarm)

        // WHEN
        launchActivity()
        onView(withId(R.id.alarm_list_item_pop_up_menu)).perform(click())
        onView(withText(R.string.delete)).inRoot(isPlatformPopup()).perform(click())
        val alarmDb = alarmDao.getAlarm(alarm.alarmId).getOrAwaitValue { }

        // THEN
        onView(withText("Removed: Alarm")).check(matches(isDisplayed()))
        assertThat(alarmDb, `is`(nullValue()))
    }

    private fun launchActivity(): ActivityScenario<MainActivity>? {
        val activityScenario = launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.alarm_list_recycler_view) as RecyclerView).itemAnimator =
                null
        }
        return activityScenario
    }

    private fun switchWithText(text: String): Matcher<View> {
        return allOf(withId(R.id.alarm_list_item_is_active), hasSibling(withText(text)))
    }

    private fun createEmptyAlarm(): Alarm = Alarm(
        vibrationType = VibrationType.NORMAL,
        snoozeLengthMinutes = 5,
        soundUri = "".toUri()
    )
}