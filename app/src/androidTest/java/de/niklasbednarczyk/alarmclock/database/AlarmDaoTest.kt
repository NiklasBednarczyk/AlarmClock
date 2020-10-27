package de.niklasbednarczyk.alarmclock.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.net.toUri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import de.niklasbednarczyk.alarmclock.MainCoroutineRule
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmDaoTest {

    private lateinit var database: AlarmClockDatabase

    private lateinit var alarmDao: AlarmDao

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AlarmClockDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        alarmDao = database.alarmDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAlarmAndGetAlarm_insertedAlarm_alarmInDb() = mainCoroutineRule.runBlockingTest {
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
    fun updateAlarmAndGetAlarm_insertAlarmAndUpdateAlarmName_alarmInDbChanged() =
        mainCoroutineRule.runBlockingTest {
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
    fun deleteAlarmAndGetAlarm_insertAndDeleteAlarm_alarmNotInDb() =
        mainCoroutineRule.runBlockingTest {
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
    fun insertAlarmAndGetAllAlarms_insertMultipleAlarmsAndGetAlarms_alarmsInDbAndList() =
        mainCoroutineRule.runBlockingTest {
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