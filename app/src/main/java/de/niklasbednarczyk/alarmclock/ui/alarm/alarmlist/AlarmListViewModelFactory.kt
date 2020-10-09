package de.niklasbednarczyk.alarmclock.ui.alarm.alarmlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.niklasbednarczyk.alarmclock.database.AlarmDao

class AlarmListViewModelFactory(private val dao: AlarmDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmListViewModel::class.java)) {
            return AlarmListViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}