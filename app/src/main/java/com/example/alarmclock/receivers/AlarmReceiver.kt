package com.example.alarmclock.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarmclock.activities.AlarmWakeUpActivity

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALARM_INTENT_KEY_ALARM_ID = "alarm_id"
        const val ALARM_INTENT_KEY_SNOOZE_COUNT = "snooze_count"
        const val ALARM_INTENT_KEY_ALARM_TYPE = "alarm_type"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val newIntent = Intent(context, AlarmWakeUpActivity::class.java).apply {
            val alarmId = intent?.extras?.getLong(ALARM_INTENT_KEY_ALARM_ID)
            val snoozeCount = intent?.extras?.getInt(ALARM_INTENT_KEY_SNOOZE_COUNT)
            val alarmType = intent?.extras?.getString(ALARM_INTENT_KEY_ALARM_TYPE)
            putExtra(ALARM_INTENT_KEY_ALARM_ID, alarmId)
            putExtra(ALARM_INTENT_KEY_SNOOZE_COUNT, snoozeCount)
            putExtra(ALARM_INTENT_KEY_ALARM_TYPE, alarmType)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context?.startActivity(newIntent)
    }


}