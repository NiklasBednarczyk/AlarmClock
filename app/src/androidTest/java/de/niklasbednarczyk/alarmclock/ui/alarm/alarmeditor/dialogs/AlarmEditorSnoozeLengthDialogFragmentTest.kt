package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs

import android.os.Bundle
import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import de.niklasbednarczyk.alarmclock.DataBindingIdlingResource
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.monitorFragment
import de.niklasbednarczyk.alarmclock.testDismissingDialog
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmEditorSnoozeLengthDialogFragmentTest {

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun fragmentDismiss_dismissesFragment() {
        with(launchFragment<AlarmEditorSnoozeLengthDialogFragment>(Bundle(), R.style.AppTheme)) {
            onFragment { fragment ->
                fragment.testDismissingDialog()
            }
        }
    }

    @Test
    fun fragment_withArgsOne_showsArgsNameAndMinuteSingular() {
        // GIVEN
        val snoozeLength = 1
        val minute = "minute"
        val bundle = Bundle().apply {
            putInt(
                AlarmEditorSnoozeLengthDialogFragment.SNOOZE_LENGTH_DIALOG_ALARM_SNOOZE_LENGTH_MINUTES_KEY,
                snoozeLength
            )
        }

        // WHEN
        val fragmentScenario =
            launchFragment<AlarmEditorSnoozeLengthDialogFragment>(bundle, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        // THEN
        onView(withParent(withId(R.id.alarm_snooze_length_dialog_snooze_length_minutes)))
            .inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withParent(withId(R.id.alarm_snooze_length_dialog_snooze_length_minutes)))
            .inRoot(isDialog()).check(matches(withText(snoozeLength.toString())))

        onView(withId(R.id.alarm_snooze_length_dialog_minute_text)).inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.alarm_snooze_length_dialog_minute_text)).inRoot(isDialog())
            .check(matches(withText(minute)))
    }

    @Test
    fun fragment_withArgsOthers_showsArgsNameAndMinutePlural() {
        // GIVEN
        val snoozeLength = 15
        val minute = "minutes"
        val bundle = Bundle().apply {
            putInt(
                AlarmEditorSnoozeLengthDialogFragment.SNOOZE_LENGTH_DIALOG_ALARM_SNOOZE_LENGTH_MINUTES_KEY,
                snoozeLength
            )
        }

        // WHEN
        val fragmentScenario =
            launchFragment<AlarmEditorSnoozeLengthDialogFragment>(bundle, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        // THEN
        onView(withParent(withId(R.id.alarm_snooze_length_dialog_snooze_length_minutes)))
            .inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withParent(withId(R.id.alarm_snooze_length_dialog_snooze_length_minutes)))
            .inRoot(isDialog()).check(matches(withText(snoozeLength.toString())))

        onView(withId(R.id.alarm_snooze_length_dialog_minute_text)).inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(R.id.alarm_snooze_length_dialog_minute_text)).inRoot(isDialog())
            .check(matches(withText(minute)))
    }

}