package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor

import android.net.Uri
import androidx.lifecycle.*
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.utils.hoursAndMinutesToTimeMinutes
import kotlinx.coroutines.*
import java.time.DayOfWeek

class AlarmEditorViewModel(
    private val dao: AlarmDao,
    alarmId: Long,
    private val defaultAlarm: Alarm
) : ViewModel() {

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
            return@map it ?: defaultAlarm
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
}