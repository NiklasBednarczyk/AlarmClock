package de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.databinding.FragmentAlarmWakeUpViewBinding
import de.niklasbednarczyk.alarmclock.enums.AlarmType
import de.niklasbednarczyk.alarmclock.enums.VibrationType
import de.niklasbednarczyk.alarmclock.utils.cancelAlarm
import de.niklasbednarczyk.alarmclock.utils.setNormalAlarm
import de.niklasbednarczyk.alarmclock.utils.snoozeAlarm

@AndroidEntryPoint
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

        val args = AlarmWakeUpViewFragmentArgs.fromBundle(requireArguments())
        val alarmId = args.alarmId
        val snoozeCount = args.snoozeCount
        val alarmType = args.alarmType

        val viewModel by viewModels<AlarmWakeUpViewViewModel>()
        viewModel.init(alarmId, snoozeCount)
        binding.alarmWakeUpViewViewModel = viewModel

        viewModel.eventVibration.observe(viewLifecycleOwner, { vibrationType ->
            val vibrator = requireActivity().getSystemService<Vibrator>()
            vibrator?.let {
                if (vibrationType == VibrationType.NONE) {
                    vibrator.cancel()
                } else {
                    vibrator.vibrate(VibrationEffect.createWaveform(vibrationType.pattern, 1))
                }
            }
        })

        viewModel.eventDismissed.observe(viewLifecycleOwner, { dismissed ->
            dismissed?.let {
                viewModel.alarm.value?.let { alarm ->
                    cancelAlarm(context, alarm, alarmType)
                    if (Uri.EMPTY != alarm.soundUri) {
                        viewModel.stopSound()
                    }
                    if (alarm.days.isNotEmpty() && alarmType == AlarmType.NORMAL) {
                        setNormalAlarm(context, alarm)
                    } else if (alarm.days.isEmpty()) {
                        viewModel.dismissOneShotAlarm()
                    }
                }
                requireActivity().finishAndRemoveTask()
            }
        })

        viewModel.eventSnoozed.observe(viewLifecycleOwner, { snoozed ->
            snoozed?.let {
                viewModel.alarm.value?.let { alarm ->
                    viewModel.snoozeCount.value?.let { snoozeCount ->
                        if (Uri.EMPTY != alarm.soundUri) {
                            viewModel.stopSound()
                        }
                        snoozeAlarm(context, alarm, snoozeCount, alarmType)
                    }
                }
            }
        })

        viewModel.alarm.observe(viewLifecycleOwner, { alarm ->
            alarm?.let {
                if (alarm.vibrationType != VibrationType.NONE) {
                    viewModel.startVibration()
                }
                if (Uri.EMPTY != alarm.soundUri) {
                    val player = createMediaPlayer(alarm.soundUri)
                    viewModel.startSound(player)
                }
                viewModel.alarm.removeObservers(viewLifecycleOwner)
            }
        })

        return binding.root
    }

    private fun createMediaPlayer(uri: Uri): MediaPlayer = MediaPlayer().apply {
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        setAudioAttributes(audioAttributes)
        isLooping = true
        setDataSource(requireContext(), uri)
        prepare()
    }
}