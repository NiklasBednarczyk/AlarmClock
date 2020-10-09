package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor

import android.net.Uri
import java.time.DayOfWeek


interface AlarmEditorListener {
    fun onDayButtonClick(dayOfWeek: DayOfWeek)

    fun onNameDialogPositiveButton(name: String)

    fun onTimeDialogTimeSet(hours: Int, minutes: Int)

    fun onSnoozeLengthDialogPositiveButton(snoozeLengthMinutes: Int)

    fun onRingtonePickerSaved(soundUri: Uri)
}