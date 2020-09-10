package com.example.alarmclock.screens.alarm.alarmwakeupview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alarmclock.database.AlarmDao

class AlarmWakeUpViewViewModelFactory(private val dao: AlarmDao, private val alarmId: Long) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmWakeUpViewViewModel::class.java)) {
            return AlarmWakeUpViewViewModel(dao, alarmId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}