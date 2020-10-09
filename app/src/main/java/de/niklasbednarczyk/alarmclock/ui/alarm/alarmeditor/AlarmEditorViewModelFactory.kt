package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao

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