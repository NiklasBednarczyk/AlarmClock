package de.niklasbednarczyk.alarmclock

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.database.AlarmClockDatabase
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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
        alarmDao = db.alarmDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAlarm() {
        val alarm = getEmptyAlarm()

        val alarmId = alarmDao.insertAlarm(alarm)
        val alarmDb = alarmDao.getAlarm(alarmId)
        alarmDb.observeForever {}

        assertEquals(alarmId, alarmDb.value?.alarmId)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetAlarm() {
        val alarmOld = getEmptyAlarm()
        val alarmId = alarmDao.insertAlarm(alarmOld)
        val newName = alarmOld.name + "new Name"
        val alarmNew = alarmOld.copy().apply {
            this.alarmId = alarmId
            this.name = newName
        }

        alarmDao.updateAlarm(alarmNew)
        val alarmDb = alarmDao.getAlarm(alarmId)
        alarmDb.observeForever {}

        assertEquals(newName, alarmDb.value?.name)
        assertNotEquals(alarmOld.name, alarmDb.value?.name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteAndGetAlarm() {
        val alarm = getEmptyAlarm()

        val alarmId = alarmDao.insertAlarm(alarm)
        alarm.apply {
            this.alarmId = alarmId
        }
        alarmDao.deleteAlarm(alarm)

        val alarmDb = alarmDao.getAlarm(alarmId)
        alarmDb.observeForever {}

        assertEquals(null, alarmDb.value)
        assertNotEquals(alarmId, alarmDb.value?.alarmId)
    }

    @Test
    @Throws(Exception::class)
    fun insertMultipleAndGetAllAlarms() {
        val alarms = listOf(
            getEmptyAlarm(),
            getEmptyAlarm(),
            getEmptyAlarm()
        )

        alarms.forEach { alarm -> alarmDao.insertAlarm(alarm) }

        val alarmsDb = alarmDao.getAllAlarms()
        alarmsDb.observeForever {}

        assertEquals(alarms.size, alarmsDb.value?.size)

    }

    private fun getEmptyAlarm(): Alarm = Alarm(
        snoozeLengthMinutes = 5,
        soundUri = "".toUri(),
        vibrationType = VibrationType.NORMAL
    )


}