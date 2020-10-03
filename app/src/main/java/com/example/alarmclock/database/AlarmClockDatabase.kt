package com.example.alarmclock.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Alarm::class], version = 3, exportSchema = false)
@TypeConverters(AlarmClockConverter::class)
abstract class AlarmClockDatabase : RoomDatabase() {

    abstract val alarmDao: AlarmDao

    companion object {

        @Volatile
        private var INSTANCE: AlarmClockDatabase? = null

        fun getInstance(context: Context): AlarmClockDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmClockDatabase::class.java,
                        "alarm_clock_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }

                return instance
            }


        }

    }

}