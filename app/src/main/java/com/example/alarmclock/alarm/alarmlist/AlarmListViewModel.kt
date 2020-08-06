package com.example.alarmclock.alarm.alarmlist

import androidx.lifecycle.ViewModel
import com.example.alarmclock.dataclasses.Alarm

class AlarmListViewModel : ViewModel() {

    val alarmList: MutableList<Alarm> = AlarmDataSource.createDataSet()

    init {
        alarmList.sortBy { alarm -> alarm.timeMinutes }
    }

}