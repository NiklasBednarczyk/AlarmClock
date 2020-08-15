package com.example.alarmclock.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {

    @Insert
    fun insertAlarm(alarm: Alarm)

    @Update
    fun updateAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm_table WHERE alarm_id = :key")
    fun getAlarm(key: Long): LiveData<Alarm>

    @Query("SELECT * FROM alarm_table ORDER BY time_minutes")
    fun getAllAlarms(): LiveData<List<Alarm>>

}