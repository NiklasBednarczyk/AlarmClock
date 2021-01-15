package de.niklasbednarczyk.alarmclock.ui.alarm.alarmwakeupview

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.databinding.FragmentAlarmWakeUpViewBinding
import de.niklasbednarczyk.alarmclock.enums.AlarmType
import de.niklasbednarczyk.alarmclock.utils.cancelAlarm
import de.niklasbednarczyk.alarmclock.utils.setNormalAlarm
import de.niklasbednarczyk.alarmclock.utils.snoozeAlarm

@AndroidEntryPoint
class AlarmWakeUpViewFragment : Fragment() {

    private lateinit var viewModel: AlarmWakeUpViewViewModel

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
        this.viewModel = viewModel
        viewModel.init(alarmId, snoozeCount)
        binding.alarmWakeUpViewViewModel = viewModel

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
                viewModel.stopSound()
                viewModel.stopVibration()
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
                viewModel.stopSound()
                viewModel.stopVibration()
            }
        })

        viewModel.alarm.observe(viewLifecycleOwner, { alarm ->
            alarm?.let {
                val vibrator = requireActivity().getSystemService<Vibrator>()
                viewModel.startVibration(vibrator)
                if (Uri.EMPTY != alarm.soundUri) {
                    val mediaPlayer = createMediaPlayer(alarm.soundUri)
                    viewModel.startSound(mediaPlayer)
                }
            }
            viewModel.alarm.removeObservers(viewLifecycleOwner)
        })

        return binding.root
    }

    override fun onDestroy() {
        viewModel.stopSound()
        viewModel.stopVibration()
        super.onDestroy()
    }

    private fun createMediaPlayer(uri: Uri) = MediaPlayer().apply {
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        setAudioAttributes(audioAttributes)
        isLooping = true
        setDataSource(requireContext(), uri)
        prepare()
    }
}