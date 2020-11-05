package de.niklasbednarczyk.alarmclock.ui.alarm.alarmlist

import androidx.recyclerview.widget.DiffUtil
import de.niklasbednarczyk.alarmclock.database.Alarm


class AlarmListDiffCallback : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.alarmId == newItem.alarmId
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }
}