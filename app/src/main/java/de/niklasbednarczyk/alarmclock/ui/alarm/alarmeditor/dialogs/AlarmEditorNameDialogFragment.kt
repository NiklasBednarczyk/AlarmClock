package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.databinding.DialogAlarmNameBinding
import de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.AlarmEditorListener


class AlarmEditorNameDialogFragment(
    private val alarmEditorListener: AlarmEditorListener,
    private val alarmName: String
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val binding: DialogAlarmNameBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.dialog_alarm_name,
                null,
                false
            )
            binding.alarmNameDialogName.setText(alarmName)

            builder
                .setView(binding.root)
                .setTitle(R.string.alarm_editor_dialog_name_title)
                .setPositiveButton(R.string.alarm_editor_dialog_name_positive_button) { _, _ ->
                    alarmEditorListener.onNameDialogPositiveButton(binding.alarmNameDialogName.text.toString())
                }
                .setNegativeButton(R.string.alarm_editor_dialog_name_negative_button) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}