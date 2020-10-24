package de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview

import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.utils.snoozeLengthMinutesToTimeMilliseconds
import kotlinx.coroutines.*

class AlarmWakeUpViewViewModel @ViewModelInject constructor(private val dao: AlarmDao) :
    ViewModel() {

    companion object {

        private const val DONE = 0L

        private const val ONE_SECOND = 1000L

    }

    private lateinit var mediaPlayer: MediaPlayer

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

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

    private val _eventVibration = MutableLiveData<VibrationType>()
    val eventVibration: LiveData<VibrationType>
        get() = _eventVibration

    private val _eventDismissed = MutableLiveData<Boolean>()
    val eventDismissed: LiveData<Boolean>
        get() = _eventDismissed

    private lateinit var snoozeTimer: CountDownTimer

    fun init(alarmId: Long, snoozeCount: Int) {
        _alarm.addSource(dao.getAlarm(alarmId), _alarm::setValue)

        _snoozeCount.value = snoozeCount
    }

    fun dismissOneShotAlarm(alarm: Alarm) {
        alarm.isActive = false
        uiScope.launch {
            withContext(Dispatchers.IO) {
                dao.updateAlarm(alarm)
            }
        }
    }

    fun onActionSnooze() {
        alarm.value?.let { alarm ->
            val snoozeLengthMilliseconds =
                snoozeLengthMinutesToTimeMilliseconds(alarm.snoozeLengthMinutes)
            snoozeTimer = object : CountDownTimer(snoozeLengthMilliseconds, ONE_SECOND) {
                override fun onTick(millisUntilFinished: Long) {
                    _snoozeTime.value = (millisUntilFinished / ONE_SECOND)
                }

                override fun onFinish() {
                    _snoozeTime.value = DONE
                }
            }

            _snoozeCount.value = _snoozeCount.value?.plus(1)
            _eventSnoozed.value = true
            stopVibration()
            snoozeTimer.start()
        }
    }

    fun onActionDismiss() {
        stopVibration()
        _eventDismissed.value = true
    }

    fun startVibration() {
        _eventVibration.value = _alarm.value?.vibrationType
    }

    private fun stopVibration() {
        _eventVibration.value = VibrationType.NONE
    }

    fun startSound(player: MediaPlayer) {
        mediaPlayer = player
        mediaPlayer.start()
    }

    fun stopSound() {
        mediaPlayer.stop()
    }
}

