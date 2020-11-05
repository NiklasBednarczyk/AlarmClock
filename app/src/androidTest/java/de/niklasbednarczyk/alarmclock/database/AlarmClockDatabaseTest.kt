package de.niklasbednarczyk.alarmclock.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import de.niklasbednarczyk.alarmclock.MainCoroutineRule
import de.niklasbednarczyk.alarmclock.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
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
class AlarmClockDatabaseTest {

    private lateinit var context: Context
    private lateinit var database: AlarmClockDatabase

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AlarmClockDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun onCreate_shouldInsertTwoAlarms() = mainCoroutineRule.runBlockingTest {
        // GIVEN
        val ioDispatcher = Dispatchers.Main

        // WHEN
        AlarmClockDatabase.onCreate(database, ioDispatcher, context)
        val alarms = database.alarmDao().getAllAlarms().getOrAwaitValue()

        // THEN
        assertThat(2, `is`(alarms.size))
    }

}