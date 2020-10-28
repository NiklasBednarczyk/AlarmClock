package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs

import android.os.Bundle
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.testDismissingDialog
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmEditorTimeDialogFragmentTest {

    @Test
    fun fragmentDismiss_dismissesFragment() {
        with(launchFragment<AlarmEditorSnoozeLengthDialogFragment>(Bundle(), R.style.AppTheme)) {
            onFragment { fragment ->
                fragment.testDismissingDialog()
            }
        }
    }

}