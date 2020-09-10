package com.example.alarmclock.activities

import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.alarmclock.R
import com.example.alarmclock.screens.alarm.alarmwakeupview.AlarmWakeUpViewFragmentArgs
import com.example.alarmclock.values.ALARM_INTENT_KEY_ALARM_ID


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
        navController.setGraph(
            navController.graph,
            AlarmWakeUpViewFragmentArgs.Builder(alarmId).build().toBundle()
        )
    }
}