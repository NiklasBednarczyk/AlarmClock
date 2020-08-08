package com.example.alarmclock.screens.alarm.alarmlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.database.Alarm
import com.example.alarmclock.databinding.ListItemAlarmBinding

class AlarmAdapter(private val alarmOnItemClickListener: AlarmOnItemClickListener) :
    ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback()) {

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder.from(parent, alarmOnItemClickListener)
    }

    class AlarmViewHolder private constructor(
        private val binding: ListItemAlarmBinding,
        private val alarmOnItemClickListener: AlarmOnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alarm: Alarm) {
            binding.alarm = alarm
            binding.isActive.setOnClickListener {
                alarmOnItemClickListener.onActiveClick(alarm)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
                alarmOnItemClickListener: AlarmOnItemClickListener
            ): AlarmViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAlarmBinding.inflate(layoutInflater, parent, false)
                return AlarmViewHolder(binding, alarmOnItemClickListener)
            }
        }
    }

    interface AlarmOnItemClickListener {
        fun onActiveClick(alarm: Alarm)
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