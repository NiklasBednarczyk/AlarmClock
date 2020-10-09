package de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.niklasbednarczyk.alarmclock.database.AlarmDao

class AlarmWakeUpViewViewModelFactory(
    private val dao: AlarmDao,
    private val alarmId: Long,
    private val snoozeCount: Int
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmWakeUpViewViewModel::class.java)) {
            return AlarmWakeUpViewViewModel(dao, alarmId, snoozeCount) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}