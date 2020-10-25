package de.niklasbednarczyk.alarmclock.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AlarmClockDatabaseTest {

    private lateinit var db: AlarmClockDatabase

    private lateinit var alarmDao: AlarmDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AlarmClockDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        alarmDao = db.alarmDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAlarmAndGetAlarm_insertedAlarm_alarmInDb() {
        // GIVEN
        val alarm = getEmptyAlarm()

        // WHEN
        val alarmId = alarmDao.insertAlarm(alarm)
        val alarmDb = alarmDao.getAlarm(alarmId).getOrAwaitValue()

        // THEN
        assertThat(alarmDb.alarmId, `is`(alarmId))
    }

    @Test
    @Throws(Exception::class)
    fun updateAlarmAndGetAlarm_insertAlarmAndUpdateAlarmName_alarmInDbChanged() {
        // GIVEN
        val alarmOld = getEmptyAlarm()
        val alarmId = alarmDao.insertAlarm(alarmOld)
        val newName = alarmOld.name + "new Name"
        val alarmNew = alarmOld.copy().apply {
            this.alarmId = alarmId
            this.name = newName
        }

        // WHEN
        alarmDao.updateAlarm(alarmNew)
        val alarmDb = alarmDao.getAlarm(alarmId).getOrAwaitValue()

        // THEN
        assertThat(alarmDb.name, `is`(newName))
        assertThat(alarmDb.name, not(alarmOld.name))
    }

    @Test
    @Throws(Exception::class)
    fun deleteAlarmAndGetAlarm_insertAndDeleteAlarm_alarmNotInDb() {
        // GIVEN
        val alarm = getEmptyAlarm()

        // WHEN
        val alarmId = alarmDao.insertAlarm(alarm)
        alarm.apply {
            this.alarmId = alarmId
        }
        alarmDao.deleteAlarm(alarm)
        val alarmDb = alarmDao.getAlarm(alarmId)
        alarmDb.getOrAwaitValue()

        // THEN
        assertThat(alarmDb.value, `is`(nullValue()))
    }

    @Test
    @Throws(Exception::class)
    fun insertAlarmAndGetAllAlarms_insertMultipleAlarmsAndGetAlarms_alarmsInDbAndList() {
        // GIVEN
        val alarms = listOf(
            getEmptyAlarm().copy().apply { timeMinutes = 1 },
            getEmptyAlarm().copy().apply { timeMinutes = 2 },
            getEmptyAlarm().copy().apply { timeMinutes = 3 }
        ).sortedBy { alarm -> alarm.timeMinutes }

        // WHEN
        alarms.forEach { alarm ->
            val alarmId = alarmDao.insertAlarm(alarm)
            alarm.alarmId = alarmId
        }
        val alarmsDb = alarmDao.getAllAlarms().getOrAwaitValue()

        // THEN
        assertThat(alarmsDb.size, `is`(alarms.size))
        assertThat(alarmsDb, `is`(alarms))
    }

    private fun getEmptyAlarm(): Alarm = Alarm(
        snoozeLengthMinutes = 5,
        soundUri = "".toUri(),
        vibrationType = VibrationType.NORMAL
    )


}