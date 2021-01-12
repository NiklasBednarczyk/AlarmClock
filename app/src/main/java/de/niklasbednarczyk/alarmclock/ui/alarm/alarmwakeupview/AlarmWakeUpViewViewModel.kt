package de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview

import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.utils.timeMinutesToTimeMilliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmWakeUpViewViewModel @ViewModelInject constructor(
    private val dao: AlarmDao,
    private val ioDispatcher: CoroutineDispatcher
) :
    ViewModel() {

    companion object {

        private const val DONE = 0L

        private const val ONE_SECOND = 1000L

    }

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

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

    private val _eventDismissed = MutableLiveData<Boolean>()
    val eventDismissed: LiveData<Boolean>
        get() = _eventDismissed

    private lateinit var snoozeTimer: CountDownTimer

    fun init(alarmId: Long, snoozeCount: Int) {
        _alarm.addSource(dao.getAlarm(alarmId), _alarm::setValue)

        _snoozeCount.value = snoozeCount
    }

    fun dismissOneShotAlarm() {
        _alarm.value?.let { alarm ->
            alarm.isActive = false
            viewModelScope.launch {
                withContext(ioDispatcher) {
                    dao.updateAlarm(alarm)
                }
            }
        }
    }

    fun onActionSnooze() {
        alarm.value?.let { alarm ->
            val snoozeLengthMilliseconds =
                timeMinutesToTimeMilliseconds(alarm.snoozeLengthMinutes)
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

    fun startVibration(vibrator: Vibrator?) {
        this.vibrator = vibrator
        _alarm.value?.let { alarm ->
            if (alarm.vibrationType != VibrationType.NONE) {
                vibrator?.vibrate(VibrationEffect.createWaveform(alarm.vibrationType.pattern, 1))
            }
        }
    }

    fun stopVibration() {
        vibrator?.cancel()
    }

    fun startSound(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer
        mediaPlayer.start()
    }

    fun stopSound() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
            mediaPlayer = null
        }

    }
}

