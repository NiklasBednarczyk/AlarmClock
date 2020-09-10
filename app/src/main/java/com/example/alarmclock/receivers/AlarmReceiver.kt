package com.example.alarmclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmclock.activities.AlarmWakeUpActivity
import com.example.alarmclock.values.ALARM_INTENT_KEY_ALARM_ID

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val newIntent = Intent(context, AlarmWakeUpActivity::class.java).apply {
            val alarmId = intent?.extras?.getLong(ALARM_INTENT_KEY_ALARM_ID)
            putExtra(ALARM_INTENT_KEY_ALARM_ID, alarmId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context?.startActivity(newIntent)
    }


}