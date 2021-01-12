package de.niklasbednarczyk.alarmclock.activities

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
    private lateinit var alarmType: AlarmType

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
        alarmType = enumValueOf(
            intent.getStringExtra(ALARM_INTENT_KEY_ALARM_TYPE) ?: AlarmType.NONE.toString()
        )

        navController.setGraph(
            navController.graph,
            AlarmWakeUpViewFragmentArgs.Builder(alarmId, snoozeCount, alarmType).build().toBundle()
        )
    }

    override fun onBackPressed() {
        if (alarmType != AlarmType.NONE) {
            val toastText = when (alarmType) {
                AlarmType.NONE -> ""
                AlarmType.NORMAL -> resources.getString(R.string.alarm_wake_up_view_fragment_toast_dismiss_normal_alarm)
                AlarmType.PREVIEW -> resources.getString(R.string.alarm_wake_up_view_fragment_toast_dismiss_preview_alarm)
            }

            Toast.makeText(applicationContext, toastText, Toast.LENGTH_SHORT).show()

            if (alarmType == AlarmType.PREVIEW) {
                finishAndRemoveTask()
            }
        }
    }
}