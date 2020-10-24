package de.niklasbednarczyk.alarmclock.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import de.niklasbednarczyk.alarmclock.database.AlarmClockDatabase
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    lateinit var database: AlarmClockDatabase

    @Singleton
    @Provides
    fun provideDataBase(
        @ApplicationContext context: Context,
        ioDispatcher: CoroutineDispatcher
    ): AlarmClockDatabase {
        database = Room.databaseBuilder(
            context.applicationContext,
            AlarmClockDatabase::class.java,
            "alarm_clock_database"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                AlarmClockDatabase.onCreate(database, ioDispatcher, context)
            }
        }).build()
        return database
    }

    @Singleton
    @Provides
    fun provideAlarmDao(alarmClockDatabase: AlarmClockDatabase): AlarmDao =
        alarmClockDatabase.alarmDao()

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

}