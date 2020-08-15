package com.example.alarmclock.screens.alarm.alarmeditor

import androidx.lifecycle.*
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.database.AlarmDao
import com.example.alarmclock.screens.alarm.calcTimeMinutes
import kotlinx.coroutines.*
import java.time.DayOfWeek

class AlarmEditorViewModel(private val dao: AlarmDao, private val alarmId: Long) : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val alarm = MediatorLiveData<Alarm>()
    fun getAlarm() = alarm

    private val _navigateToAlarmList = MutableLiveData<Boolean?>()
    val navigateToAlarmList: LiveData<Boolean?>
        get() = _navigateToAlarmList

    val alarmEditorListener = object : AlarmEditorListener {
        override fun onDayButtonClick(dayOfWeek: DayOfWeek) {
            alarm.value?.let {
                if (it.days.contains(dayOfWeek)) {
                    it.days.remove(dayOfWeek)
                } else {
                    it.days.add(dayOfWeek)
                }
            }
        }

        override fun onNameDialogPositiveButton(name: String) {
            alarm.value?.name = name
            alarm.postValue(alarm.value)
        }

        override fun onTimeDialogTimeSet(hours: Int, minutes: Int) {
            alarm.value?.timeMinutes = calcTimeMinutes(hours, minutes)
            alarm.postValue(alarm.value)
        }
    }

    init {
        val alarmLiveData = Transformations.map(dao.getAlarm(alarmId)) {
            return@map it ?: getDefaultAlarm()
        }
        alarm.addSource(alarmLiveData, alarm::setValue)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onActionSave() {
        alarm.value?.let {
            uiScope.launch {
                it.isActive = true
                if (it.alarmId == 0L) {
                    insertAlarm(it)
                } else {
                    updateAlarm(it)
                }
                withContext(Dispatchers.Main) {
                    startNavigatingToAlarmList()
                }
            }
        }
    }

    private fun startNavigatingToAlarmList() {
        _navigateToAlarmList.value = true
    }

    fun doneNavigatingToAlarmList() {
        _navigateToAlarmList.value = null
    }

    private suspend fun insertAlarm(alarm: Alarm) {
        withContext(Dispatchers.IO) {
            dao.insertAlarm(alarm)
        }
    }

    private suspend fun updateAlarm(alarm: Alarm) {
        withContext(Dispatchers.IO) {
            dao.updateAlarm(alarm)
        }
    }

    private fun getDefaultAlarm(): Alarm {
        val defaultIsActive = true
        val defaultName = "Default Name"
        val defaultTimeMinutes = 420
        val defaultDays = mutableListOf<DayOfWeek>()
        return Alarm(
            isActive = defaultIsActive,
            name = defaultName,
            timeMinutes = defaultTimeMinutes,
            days = defaultDays
        )
    }
}