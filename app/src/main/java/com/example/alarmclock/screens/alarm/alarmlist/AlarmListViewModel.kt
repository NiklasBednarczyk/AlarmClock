package com.example.alarmclock.screens.alarm.alarmlist

import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alarmclock.R
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.database.AlarmDao
import kotlinx.coroutines.*

class AlarmListViewModel(private val dao: AlarmDao) : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val alarms = dao.getAllAlarms()

    private val _navigateToAlarmEditor = MutableLiveData<Long>()
    val navigateToAlarmEditor: LiveData<Long>
        get() = _navigateToAlarmEditor

    private val _showItemPopUpMenu = MutableLiveData<Pair<View?, Alarm?>>()
    val showItemPopUpMenu: LiveData<Pair<View?, Alarm?>>
        get() = _showItemPopUpMenu

    private val _eventIsActiveChanged = MutableLiveData<Alarm>()
    val eventIsActiveChanged: LiveData<Alarm>
        get() = _eventIsActiveChanged

    private val _eventDeleteAlarm = MutableLiveData<Alarm>()
    val eventDeleteAlarm: LiveData<Alarm>
        get() = _eventDeleteAlarm

    private val _eventInsertAlarm = MutableLiveData<Alarm>()
    val eventInsertAlarm: LiveData<Alarm>
        get() = _eventInsertAlarm

    val alarmOnItemClickListener = object : AlarmListAdapter.AlarmOnItemClickListener {
        override fun onActiveClick(alarm: Alarm) {
            alarm.isActive = !alarm.isActive
            _eventIsActiveChanged.value = alarm
            updateAlarm(alarm)
        }

        override fun onCardViewClick(alarmId: Long) {
            startNavigatingToAlarmEditor(alarmId)
        }

        override fun onPopUpMenuClick(view: View, alarm: Alarm) {
            startShowingItemPopUpMenu(view, alarm)
        }
    }

    val alarmItemPopupMenuListener = PopupMenu.OnMenuItemClickListener { item ->
        val alarm = showItemPopUpMenu.value?.second
        if (alarm != null) {
            when (item?.itemId) {
                R.id.action_edit -> {
                    startNavigatingToAlarmEditor(alarm.alarmId)
                    true
                }
                R.id.action_duplicate -> {
                    alarm.alarmId = 0L
                    insertAlarm(alarm)
                    true
                }
                R.id.action_delete -> {
                    deleteAlarm(alarm)
                    true
                }
                else -> false
            }
        } else {
            false
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun insertAlarm(alarm: Alarm) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                alarm.alarmId = dao.insertAlarm(alarm)
            }
            _eventInsertAlarm.value = alarm
        }

    }

    private fun updateAlarm(alarm: Alarm) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dao.updateAlarm(alarm)
            }
        }
    }

    private fun deleteAlarm(alarm: Alarm) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteAlarm(alarm)
            }
        }
        _eventDeleteAlarm.value = alarm
    }

    fun startNavigatingToAlarmEditor(alarmId: Long) {
        _navigateToAlarmEditor.value = alarmId
    }

    fun doneNavigatingToAlarmEditor() {
        _navigateToAlarmEditor.value = null
    }

    fun startShowingItemPopUpMenu(view: View, alarm: Alarm) {
        _showItemPopUpMenu.value = Pair(view, alarm)
    }

    fun doneShowingItemPopUpMenu() {
        _showItemPopUpMenu.value = Pair(null, showItemPopUpMenu.value?.second)
    }

    fun doneIsActive() {
        _eventIsActiveChanged.value = null
    }

    fun doneDeleteAlarm() {
        _eventDeleteAlarm.value = null
    }

    fun doneInsertAlarm() {
        _eventInsertAlarm.value = null
    }
}