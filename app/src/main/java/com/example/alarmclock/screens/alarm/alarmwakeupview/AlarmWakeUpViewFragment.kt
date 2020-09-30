package com.example.alarmclock.screens.alarm.alarmwakeupview

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmClockDatabase
import com.example.alarmclock.databinding.FragmentAlarmWakeUpViewBinding
import com.example.alarmclock.utils.setNormalAlarm


class AlarmWakeUpViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAlarmWakeUpViewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_alarm_wake_up_view,
            container,
            false
        )

        binding.lifecycleOwner = this

        val args = AlarmWakeUpViewFragmentArgs.fromBundle(arguments!!)
        val alarmId = args.alarmId

        val application = requireActivity()
        val dao = AlarmClockDatabase.getInstance(application).alarmDao
        val viewModelFactory = AlarmWakeUpViewViewModelFactory(dao, alarmId)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(AlarmWakeUpViewViewModel::class.java)
        binding.alarmWakeUpViewViewModel = viewModel

        viewModel.eventVibration.observe(viewLifecycleOwner, { vibrationType ->
            val vibrator = requireActivity().getSystemService<Vibrator>()
            vibrator?.let {
                if (vibrationType != AlarmWakeUpViewVibrationType.NO_VIBRATION) {
                    vibrator.vibrate(VibrationEffect.createWaveform(vibrationType.pattern, 1))
                } else {
                    vibrator.cancel()
                }
            }
        })

        viewModel.eventDismissed.observe(viewLifecycleOwner, { dismissed ->
            if (dismissed) {
                viewModel.alarm.value?.let { alarm ->
                    if (alarm.days.isNotEmpty()) {
                        setNormalAlarm(context, alarm)
                    }
                }
                requireActivity().finish()
            }
        })

        return binding.root
    }

}