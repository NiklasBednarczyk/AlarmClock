package de.niklasbednarczyk.alarmclock.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.niklasbednarczyk.alarmclock.utils.WEEKDAYS
import de.niklasbednarczyk.alarmclock.utils.WEEKEND
import de.niklasbednarczyk.alarmclock.utils.getDefaultAlarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(entities = [Alarm::class], version = 7, exportSchema = true)
@TypeConverters(AlarmClockTypeConverter::class)
abstract class AlarmClockDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {

        fun onCreate(database: AlarmClockDatabase, context: Context) {
            val initialAlarms = getInitialAlarms(context)
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    initialAlarms.forEach { initialAlarm ->
                        database.alarmDao().insertAlarm(initialAlarm)
                    }
                }
            }
        }

        private fun getInitialAlarms(context: Context): List<Alarm> {
            val defaultAlarm = getDefaultAlarm(context)
            val alarmWeekdays = defaultAlarm.copy().apply {
                timeMinutes = 6.times(60)
                days = WEEKDAYS.toMutableList()
            }
            val alarmWeekend = defaultAlarm.copy().apply {
                timeMinutes = 8.times(60)
                days = WEEKEND.toMutableList()
            }
            return listOf(alarmWeekdays, alarmWeekend)
        }
    }

}
