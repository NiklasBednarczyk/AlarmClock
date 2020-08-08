package com.example.alarmclock.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDao {

    @Update
    fun update(alarm: Alarm)

    @Query("SELECT * FROM alarm_table ORDER BY time_minutes")
    fun getAllAlarms(): LiveData<List<Alarm>>

}