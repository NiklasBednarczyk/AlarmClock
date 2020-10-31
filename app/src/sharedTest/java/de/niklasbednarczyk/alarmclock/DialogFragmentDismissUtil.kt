package de.niklasbednarczyk.alarmclock

import androidx.annotation.VisibleForTesting
import androidx.fragment.app.DialogFragment
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.hamcrest.core.IsNot
import org.hamcrest.core.IsNull

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun DialogFragment.testDismissingDialog() {
    assertThat(this.dialog, Is.`is`(IsNot.not(IsNull.nullValue())))
    assertThat(this.requireDialog().isShowing, Is.`is`(true))
    this.dismiss()
    this.parentFragmentManager.executePendingTransactions()
    assertThat(this.dialog, Is.`is`(IsNull.nullValue()))
}