package de.niklasbednarczyk.alarmclock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmDao

class FakeAlarmDao() : AlarmDao {

    private val alarms: MutableList<Alarm> = mutableListOf()
    private var alarmIdCounter: Long = 0L

    override fun insertAlarm(alarm: Alarm): Long {
        alarmIdCounter++
        alarm.alarmId = alarmIdCounter
        alarms.add(alarm)
        return alarmIdCounter
    }

    override fun updateAlarm(alarm: Alarm) {
        alarms.mapIndexed { index, oldAlarm ->
            if (oldAlarm.alarmId == alarm.alarmId) {
                alarms[index] = alarm
            }
        }
    }

    override fun deleteAlarm(alarm: Alarm) {
        alarms.remove(alarm)
    }

    override fun getAlarm(key: Long): LiveData<Alarm> {
        var alarm: Alarm? = null
        alarms.firstOrNull { it.alarmId == key }?.let { alarm = it }
        return MutableLiveData(alarm)
    }

    override fun getAllAlarms(): LiveData<List<Alarm>> {
        return MutableLiveData(alarms)
    }

}