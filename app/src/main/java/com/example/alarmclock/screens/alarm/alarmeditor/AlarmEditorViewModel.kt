package com.example.alarmclock.screens.alarm.alarmeditor

import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.*
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.database.AlarmDao
import com.example.alarmclock.utils.hoursAndMinutesToTimeMinutes
import kotlinx.coroutines.*
import java.time.DayOfWeek

class AlarmEditorViewModel(private val dao: AlarmDao, alarmId: Long) : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _alarm = MediatorLiveData<Alarm>()
    val alarm: LiveData<Alarm>
        get() = _alarm

    private val _navigateToAlarmList = MutableLiveData<Alarm>()
    val navigateToAlarmList: LiveData<Alarm>
        get() = _navigateToAlarmList

    val alarmEditorListener = object : AlarmEditorListener {
        override fun onDayButtonClick(dayOfWeek: DayOfWeek) {
            _alarm.value?.let { alarm ->
                if (alarm.days.contains(dayOfWeek)) {
                    alarm.days.remove(dayOfWeek)
                } else {
                    alarm.days.add(dayOfWeek)
                }
                _alarm.postValue(alarm)
            }
        }

        override fun onNameDialogPositiveButton(name: String) {
            _alarm.value?.let { alarm ->
                alarm.name = name
                _alarm.postValue(alarm)
            }

        }

        override fun onRingtonePickerSaved(soundUri: Uri) {
            _alarm.value?.let { alarm ->
                alarm.soundUri = soundUri
                _alarm.postValue(alarm)
            }
        }

        override fun onTimeDialogTimeSet(hours: Int, minutes: Int) {
            _alarm.value?.let { alarm ->
                alarm.timeMinutes = hoursAndMinutesToTimeMinutes(hours, minutes)
                _alarm.postValue(alarm)
            }
        }

        override fun onSnoozeLengthDialogPositiveButton(snoozeLengthMinutes: Int) {
            _alarm.value?.let { alarm ->
                alarm.snoozeLengthMinutes = snoozeLengthMinutes
                _alarm.postValue(alarm)
            }
        }
    }

    init {
        val alarmLiveData = Transformations.map(dao.getAlarm(alarmId)) {
            return@map it ?: getDefaultAlarm()
        }
        _alarm.addSource(alarmLiveData, _alarm::setValue)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onActionSave() {
        _alarm.value?.let { alarm ->
            uiScope.launch {
                alarm.isActive = true
                if (alarm.alarmId == 0L) {
                    alarm.alarmId = insertAlarm(alarm)
                } else {
                    updateAlarm(alarm)
                }
                startNavigatingToAlarmList(alarm)
            }
        }
    }

    private fun startNavigatingToAlarmList(alarm: Alarm) {
        _navigateToAlarmList.value = alarm
    }

    fun doneNavigatingToAlarmList() {
        _navigateToAlarmList.value = null
    }

    private suspend fun insertAlarm(alarm: Alarm): Long {
        var alarmId: Long
        withContext(Dispatchers.IO) {
            alarmId = dao.insertAlarm(alarm)
        }
        return alarmId
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
        val defaultSnoozeLengthMinutes = 5
        val defaultVibrate = true
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        return Alarm(
            isActive = defaultIsActive,
            name = defaultName,
            timeMinutes = defaultTimeMinutes,
            days = defaultDays,
            snoozeLengthMinutes = defaultSnoozeLengthMinutes,
            vibrate = defaultVibrate,
            soundUri = defaultSoundUri
        )
    }
}