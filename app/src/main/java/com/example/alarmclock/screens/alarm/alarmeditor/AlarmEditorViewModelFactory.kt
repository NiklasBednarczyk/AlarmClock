package com.example.alarmclock.screens.alarm.alarmeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.database.AlarmDao

class AlarmEditorViewModelFactory(
    private val dao: AlarmDao,
    private val alarmId: Long,
    private val defaultAlarm: Alarm
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmEditorViewModel::class.java)) {
            return AlarmEditorViewModel(dao, alarmId, defaultAlarm) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}