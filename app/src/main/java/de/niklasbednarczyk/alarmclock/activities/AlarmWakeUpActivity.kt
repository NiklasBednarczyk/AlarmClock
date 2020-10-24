package de.niklasbednarczyk.alarmclock.activities

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.enums.AlarmType
import de.niklasbednarczyk.alarmclock.receivers.AlarmReceiver.Companion.ALARM_INTENT_KEY_ALARM_ID
import de.niklasbednarczyk.alarmclock.receivers.AlarmReceiver.Companion.ALARM_INTENT_KEY_ALARM_TYPE
import de.niklasbednarczyk.alarmclock.receivers.AlarmReceiver.Companion.ALARM_INTENT_KEY_SNOOZE_COUNT
import de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview.AlarmWakeUpViewFragmentArgs

@AndroidEntryPoint
class AlarmWakeUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_wake_up)

        setShowWhenLocked(true)
        setTurnScreenOn(true)
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)

        val navController = findNavController(R.id.alarm_wake_up_nav_host_fragment)
        val alarmId = intent.getLongExtra(ALARM_INTENT_KEY_ALARM_ID, -1)
        val snoozeCount = intent.getIntExtra(ALARM_INTENT_KEY_SNOOZE_COUNT, -1)
        val alarmType = enumValueOf<AlarmType>(
            intent.getStringExtra(ALARM_INTENT_KEY_ALARM_TYPE)
                ?: AlarmType.NONE.toString()
        )

        navController.setGraph(
            navController.graph,
            AlarmWakeUpViewFragmentArgs.Builder(alarmId, snoozeCount, alarmType).build().toBundle()
        )
    }
}