package com.example.alarmclock.screens.alarm.alarmeditor.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.alarmclock.R
import com.example.alarmclock.screens.alarm.alarmeditor.AlarmEditorListener
import kotlinx.android.synthetic.main.dialog_alarm_name.view.*


class AlarmEditorNameDialogFragment(
    private val alarmEditorListener: AlarmEditorListener,
    private val alarmName: String
) : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_alarm_name, null)
            view.name.setText(alarmName)

            builder
                .setView(view)
                .setTitle(R.string.alarm_name)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    alarmEditorListener.onNameDialogPositiveButton(view.name.text.toString())
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}