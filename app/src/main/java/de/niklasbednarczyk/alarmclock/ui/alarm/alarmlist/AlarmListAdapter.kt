package de.niklasbednarczyk.alarmclock.ui.alarm.alarmlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.niklasbednarczyk.alarmclock.database.Alarm
import de.niklasbednarczyk.alarmclock.databinding.ListItemAlarmBinding

class AlarmListAdapter(private val alarmOnItemClickListener: AlarmOnItemClickListener) :
    ListAdapter<Alarm, AlarmListAdapter.AlarmViewHolder>(AlarmListDiffCallback()) {

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
            binding.alarmOnItemClickListener = alarmOnItemClickListener
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
        fun onCardViewClick(alarmId: Long)

        fun onActiveClick(alarm: Alarm)

        fun onPopUpMenuClick(view: View, alarm: Alarm)
    }
}
