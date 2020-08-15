package com.example.alarmclock.screens.alarm.alarmeditor.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.alarmclock.screens.alarm.alarmeditor.AlarmEditorListener

class AlarmEditorTimeDialogFragment(private val alarmEditorListener: AlarmEditorListener) :
    DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        alarmEditorListener.onTimeDialogTimeSet(hourOfDay, minute)
    }
}