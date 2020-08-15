package com.example.alarmclock.screens.alarm.alarmeditor

import java.time.DayOfWeek


interface AlarmEditorListener {
    fun onDayButtonClick(dayOfWeek: DayOfWeek)

    fun onNameDialogPositiveButton(name: String)

    fun onTimeDialogTimeSet(hours: Int, minutes: Int)
}