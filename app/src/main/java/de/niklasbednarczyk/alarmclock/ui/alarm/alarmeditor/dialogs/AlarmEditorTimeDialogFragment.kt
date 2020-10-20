package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.AlarmEditorListener
import de.niklasbednarczyk.alarmclock.utils.calendarToCalendarHoursAndMinutes
import de.niklasbednarczyk.alarmclock.utils.timeMinutesToHoursAndMinutes
import java.util.*

class AlarmEditorTimeDialogFragment(
    private val alarmEditorListener: AlarmEditorListener,
    private val alarmTimeMinutes: Int
) :
    DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val (alarmHours, alarmMinutes) = timeMinutesToHoursAndMinutes(alarmTimeMinutes)
        return TimePickerDialog(
            activity,
            this,
            alarmHours,
            alarmMinutes,
            DateFormat.is24HourFormat(activity)
        ).apply {
            setButton(
                DialogInterface.BUTTON_NEUTRAL,
                resources.getString(R.string.alarm_editor_dialog_time_neutral_button)
            ) { _, _ -> }
            setOnShowListener {
                val button = this.getButton(DialogInterface.BUTTON_NEUTRAL)
                button.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val (calendarHours, calendarMinutes) = calendarToCalendarHoursAndMinutes(
                        calendar
                    )
                    this.updateTime(calendarHours, calendarMinutes)
                }
            }
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        alarmEditorListener.onTimeDialogTimeSet(hourOfDay, minute)
    }
}