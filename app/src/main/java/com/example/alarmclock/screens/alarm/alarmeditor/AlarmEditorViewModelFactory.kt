package com.example.alarmclock.screens.alarm.alarmeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alarmclock.database.AlarmDao

class AlarmEditorViewModelFactory(private val dao: AlarmDao, private val alarmId: Long) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmEditorViewModel::class.java)) {
            return AlarmEditorViewModel(dao, alarmId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}