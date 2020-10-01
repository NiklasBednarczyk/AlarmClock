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

    private val _navigateToSettings = MutableLiveData<Boolean>()
    val navigateToSettings: LiveData<Boolean>
        get() = _navigateToSettings

    private val _showItemPopUpMenu = MutableLiveData<Pair<View?, Alarm?>>()
    val showItemPopUpMenu: LiveData<Pair<View?, Alarm?>>
        get() = _showItemPopUpMenu

    private val _eventDeleteAlarm = MutableLiveData<Alarm>()
    val eventDeleteAlarm: LiveData<Alarm>
        get() = _eventDeleteAlarm

    private val _eventSetNormalAlarm = MutableLiveData<Alarm>()
    val eventSetNormalAlarm: LiveData<Alarm>
        get() = _eventSetNormalAlarm

    private val _eventSetPreviewAlarm = MutableLiveData<Alarm>()
    val eventSetPreviewAlarm: LiveData<Alarm>
        get() = _eventSetPreviewAlarm

    private val _eventCancelAlarm = MutableLiveData<Alarm>()
    val eventCancelAlarm: LiveData<Alarm>
        get() = _eventCancelAlarm

    val alarmOnItemClickListener = object : AlarmListAdapter.AlarmOnItemClickListener {
        override fun onActiveClick(alarm: Alarm) {
            alarm.isActive = !alarm.isActive
            if (alarm.isActive) {
                startSettingNormalAlarm(alarm)
            } else {
                startCancellingAlarm(alarm)
            }
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
                R.id.action_preview -> {
                    startSettingPreviewAlarm(alarm)
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
            startSettingNormalAlarm(alarm)
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
        startDeletingAlarm(alarm)
    }

    fun startNavigatingToAlarmEditor(alarmId: Long) {
        _navigateToAlarmEditor.value = alarmId
    }

    fun doneNavigatingToAlarmEditor() {
        _navigateToAlarmEditor.value = null
    }

    fun startNavigatingToSettings() {
        _navigateToSettings.value = true
    }

    fun doneNavigatingToSettings() {
        _navigateToSettings.value = null
    }

    fun startShowingItemPopUpMenu(view: View, alarm: Alarm) {
        _showItemPopUpMenu.value = Pair(view, alarm)
    }

    fun doneShowingItemPopUpMenu() {
        _showItemPopUpMenu.value = Pair(null, showItemPopUpMenu.value?.second)
    }

    private fun startDeletingAlarm(alarm: Alarm) {
        _eventDeleteAlarm.value = alarm
    }

    fun doneDeletingAlarm() {
        _eventDeleteAlarm.value = null
    }

    fun startSettingNormalAlarm(alarm: Alarm) {
        _eventSetNormalAlarm.value = alarm
    }

    fun doneSettingNormalAlarm() {
        _eventSetNormalAlarm.value = null
    }

    private fun startSettingPreviewAlarm(alarm: Alarm) {
        _eventSetPreviewAlarm.value = alarm
    }

    fun doneSettingPreviewAlarm() {
        _eventSetPreviewAlarm.value = null
    }

    fun startCancellingAlarm(alarm: Alarm) {
        _eventCancelAlarm.value = alarm
    }

    fun doneCancellingAlarm() {
        _eventCancelAlarm.value = null
    }

}