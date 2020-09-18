package com.example.alarmclock.screens.alarm.alarmwakeupview

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.database.AlarmDao

class AlarmWakeUpViewViewModel(dao: AlarmDao, alarmId: Long) : ViewModel() {

    companion object {

        private const val DONE = 0L

        private const val ONE_SECOND = 1000L

        private const val COUNTDOWN_TIME = 10000L
    }

    private val _alarm = MediatorLiveData<Alarm>()
    val alarm: LiveData<Alarm>
        get() = _alarm

    private val _snoozeTime = MutableLiveData<Long>()
    val snoozeTime: LiveData<Long>
        get() = _snoozeTime

    private val _snoozeCount = MutableLiveData<Int>()
    val snoozeCount: LiveData<Int>
        get() = _snoozeCount

    private val _eventSnoozed = MutableLiveData<Boolean>()
    val eventSnoozed: LiveData<Boolean>
        get() = _eventSnoozed

    private val _eventVibration = MutableLiveData<AlarmWakeUpViewVibrationType>()
    val eventVibration: LiveData<AlarmWakeUpViewVibrationType>
        get() = _eventVibration

    private val _eventDismissed = MutableLiveData<Boolean>()
    val eventDismissed: LiveData<Boolean>
        get() = _eventDismissed

    private val snoozeTimer: CountDownTimer

    init {
        _alarm.addSource(dao.getAlarm(alarmId), _alarm::setValue)

        _snoozeCount.value = 0

        _eventVibration.value = AlarmWakeUpViewVibrationType.ALARM

        snoozeTimer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _snoozeTime.value = (millisUntilFinished / ONE_SECOND)
            }

            override fun onFinish() {
                _snoozeTime.value = DONE
                _eventSnoozed.value = false
                _eventVibration.value = AlarmWakeUpViewVibrationType.ALARM
            }
        }
    }

    fun onActionSnooze() {
        _snoozeCount.value = _snoozeCount.value?.plus(1)
        _eventSnoozed.value = true
        _eventVibration.value = AlarmWakeUpViewVibrationType.NO_VIBRATION
        snoozeTimer.start()
    }

    fun onActionDismiss() {
        _eventVibration.value = AlarmWakeUpViewVibrationType.NO_VIBRATION
        _eventDismissed.value = true
    }
}

