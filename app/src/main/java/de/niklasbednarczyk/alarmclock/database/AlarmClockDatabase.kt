package de.niklasbednarczyk.alarmclock.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import de.niklasbednarczyk.alarmclock.utils.WEEKDAYS
import de.niklasbednarczyk.alarmclock.utils.WEEKEND
import de.niklasbednarczyk.alarmclock.utils.getDefaultAlarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(entities = [Alarm::class], version = 7, exportSchema = false)
@TypeConverters(AlarmClockTypeConverter::class)
abstract class AlarmClockDatabase : RoomDatabase() {

    abstract val alarmDao: AlarmDao


    companion object {

        @Volatile
        private var INSTANCE: AlarmClockDatabase? = null

        fun getInstance(context: Context): AlarmClockDatabase {
            synchronized(this) {
                var instance = INSTANCE

                val callback = object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        val initialAlarms = getInitialAlarms(context)
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                initialAlarms.forEach { initialAlarm ->
                                    instance?.alarmDao?.insertAlarm(initialAlarm)
                                }
                            }
                        }
                        super.onCreate(db)
                    }
                }

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmClockDatabase::class.java,
                        "alarm_clock_database"
                    ).addCallback(callback).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }

        private fun getInitialAlarms(context: Context?): List<Alarm> {
            return if (context != null) {
                val defaultAlarm = getDefaultAlarm(context)
                val alarmWeekdays = defaultAlarm.copy().apply {
                    timeMinutes = 6.times(60)
                    days = WEEKDAYS.toMutableList()
                }
                val alarmWeekend = defaultAlarm.copy().apply {
                    timeMinutes = 8.times(60)
                    days = WEEKEND.toMutableList()
                }
                listOf(alarmWeekdays, alarmWeekend)
            } else {
                return emptyList()
            }
        }

    }

}