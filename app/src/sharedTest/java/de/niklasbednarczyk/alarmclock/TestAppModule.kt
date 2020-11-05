package de.niklasbednarczyk.alarmclock

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import de.niklasbednarczyk.alarmclock.database.AlarmDao
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class TestAppModule {

    @Singleton
    @Binds
    abstract fun bindAlarmDao(alarmDao: FakeAlarmDao): AlarmDao

}