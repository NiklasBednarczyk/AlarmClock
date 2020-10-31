package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.databinding.DialogAlarmSnoozeLengthBinding
import de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.AlarmEditorListener

class AlarmEditorSnoozeLengthDialogFragment : DialogFragment() {

    companion object {

        const val SNOOZE_LENGTH_DIALOG_ALARM_SNOOZE_LENGTH_MINUTES_KEY = "alarm_snooze_length"

        const val SNOOZE_LENGTH_MINUTES_MIN_VALUE = 1
        const val SNOOZE_LENGTH_MINUTES_MAX_VALUE = 30

        fun newInstance(
            alarmEditorListener: AlarmEditorListener,
            alarmSnoozeLengthMinutes: Int
        ): AlarmEditorSnoozeLengthDialogFragment {
            val fragment = AlarmEditorSnoozeLengthDialogFragment()
            val args = Bundle().apply {
                putInt(
                    SNOOZE_LENGTH_DIALOG_ALARM_SNOOZE_LENGTH_MINUTES_KEY, alarmSnoozeLengthMinutes
                )
            }
            fragment.arguments = args
            fragment.alarmEditorListener = alarmEditorListener
            return fragment
        }
    }

    private lateinit var alarmEditorListener: AlarmEditorListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            val builder = AlertDialog.Builder(it)

            val alarmSnoozeLengthMinutes = arguments?.getInt(
                SNOOZE_LENGTH_DIALOG_ALARM_SNOOZE_LENGTH_MINUTES_KEY
            ) ?: 5

            val inflater = requireActivity().layoutInflater
            val binding: DialogAlarmSnoozeLengthBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_alarm_snooze_length,
                null,
                false
            )

            binding.alarmSnoozeLengthDialogMinuteText.text = getMinuteText(alarmSnoozeLengthMinutes)

            binding.alarmSnoozeLengthDialogSnoozeLengthMinutes.minValue =
                SNOOZE_LENGTH_MINUTES_MIN_VALUE
            binding.alarmSnoozeLengthDialogSnoozeLengthMinutes.maxValue =
                SNOOZE_LENGTH_MINUTES_MAX_VALUE
            binding.alarmSnoozeLengthDialogSnoozeLengthMinutes.value = alarmSnoozeLengthMinutes
            binding.alarmSnoozeLengthDialogSnoozeLengthMinutes.setOnValueChangedListener { _, _, newAlarmSnoozeLengthMinutes ->
                binding.alarmSnoozeLengthDialogMinuteText.text =
                    getMinuteText(newAlarmSnoozeLengthMinutes)
            }

            builder
                .setView(binding.root)
                .setTitle(R.string.alarm_editor_dialog_snooze_length_title)
                .setPositiveButton(R.string.alarm_editor_dialog_snooze_length_positive_button) { _, _ ->
                    alarmEditorListener.onSnoozeLengthDialogPositiveButton(binding.alarmSnoozeLengthDialogSnoozeLengthMinutes.value)
                }
                .setNegativeButton(R.string.alarm_editor_dialog_snooze_length_negative_button) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun getMinuteText(alarmSnoozeLengthMinutes: Int): String = String.format(
        resources.getQuantityString(
            R.plurals.alarm_minutes,
            alarmSnoozeLengthMinutes,
            alarmSnoozeLengthMinutes
        )
    )

}