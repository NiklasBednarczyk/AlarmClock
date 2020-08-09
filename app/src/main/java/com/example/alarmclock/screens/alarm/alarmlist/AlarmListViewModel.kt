package com.example.alarmclock.screens.alarm.alarmlist

import androidx.lifecycle.ViewModel
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.database.AlarmDao
import kotlinx.coroutines.*

class AlarmListViewModel(private val dao: AlarmDao) : ViewModel() {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val alarms = dao.getAllAlarms()

    val alarmOnItemClickListener = object : AlarmAdapter.AlarmOnItemClickListener {
        override fun onActiveClick(alarm: Alarm) {
            uiScope.launch {
                alarm.isActive = !alarm.isActive
                updateAlarm(alarm)
            }
        }
    }

    private suspend fun updateAlarm(alarm: Alarm) {
        withContext(Dispatchers.IO) {
            dao.update(alarm)
        }
    }
}