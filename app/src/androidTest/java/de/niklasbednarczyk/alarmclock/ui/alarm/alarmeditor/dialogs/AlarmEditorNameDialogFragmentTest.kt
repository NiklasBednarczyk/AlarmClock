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
class AlarmEditorNameDialogFragmentTest {

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
        with(launchFragment<AlarmEditorNameDialogFragment>(Bundle(), R.style.AppTheme)) {
            onFragment { fragment ->
                fragment.testDismissingDialog()
            }
        }
    }

    @Test
    fun fragment_withArgs_showsArgsName() {
        // GIVEN
        val alarmName = "Alarm Name"
        val bundle = Bundle().apply {
            putString(
                AlarmEditorNameDialogFragment.NAME_DIALOG_ALARM_NAME_KEY,
                alarmName
            )
        }

        // WHEN
        val fragmentScenario =
            launchFragment<AlarmEditorNameDialogFragment>(bundle, R.style.AppTheme)
        dataBindingIdlingResource.monitorFragment(fragmentScenario)

        // THEN
        onView(withId(R.id.alarm_name_dialog_name)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withId(R.id.alarm_name_dialog_name)).inRoot(isDialog())
            .check(matches(withText(alarmName)))
    }

}