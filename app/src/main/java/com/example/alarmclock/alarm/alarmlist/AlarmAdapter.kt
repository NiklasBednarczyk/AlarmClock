package com.example.alarmclock.alarm.alarmlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.databinding.ListItemAlarmBinding
import com.example.alarmclock.dataclasses.Alarm

class AlarmAdapter :
    ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(
        AlarmDiffCallback()
    ) {

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder.from(
            parent
        )
    }

    class AlarmViewHolder private constructor(private val binding: ListItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(Alarm: Alarm) {
            binding.alarm = Alarm
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AlarmViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAlarmBinding.inflate(layoutInflater, parent, false)
                return AlarmViewHolder(binding)
            }
        }
    }
}

class AlarmDiffCallback : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem.alarmId == newItem.alarmId
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }
}