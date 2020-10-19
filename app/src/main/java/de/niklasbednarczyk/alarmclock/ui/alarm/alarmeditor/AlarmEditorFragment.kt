package de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.database.AlarmClockDatabase
import de.niklasbednarczyk.alarmclock.databinding.FragmentAlarmEditorBinding
import de.niklasbednarczyk.alarmclock.enums.AlarmPropertyType
import de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs.AlarmEditorNameDialogFragment
import de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs.AlarmEditorSnoozeLengthDialogFragment
import de.niklasbednarczyk.alarmclock.ui.alarm.alarmeditor.dialogs.AlarmEditorTimeDialogFragment
import de.niklasbednarczyk.alarmclock.utils.getDefaultAlarm
import de.niklasbednarczyk.alarmclock.utils.setNormalAlarm

class AlarmEditorFragment : Fragment() {

    companion object {
        const val INTENT_RINGTONE_PICKER_REQUEST_CODE = 999
    }

    private lateinit var viewModel: AlarmEditorViewModel

    private lateinit var supportFragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAlarmEditorBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_alarm_editor,
            container,
            false
        )

        setHasOptionsMenu(true)

        binding.lifecycleOwner = this

        val application = requireActivity()

        val dao = AlarmClockDatabase.getInstance(application).alarmDao

        val defaultAlarm = getDefaultAlarm(requireContext())

        val args = AlarmEditorFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = AlarmEditorViewModelFactory(dao, args.alarmId, defaultAlarm)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmEditorViewModel::class.java)
        binding.alarmEditorViewModel = viewModel
        binding.alarmEditorFragment = this

        supportFragmentManager = application.supportFragmentManager

        viewModel.navigateToAlarmList.observe(viewLifecycleOwner, { alarm ->
            if (alarm != null) {
                setNormalAlarm(context, alarm)
                findNavController().navigate(
                    AlarmEditorFragmentDirections.actionAlarmEditorFragmentToAlarmListFragment()
                )
                viewModel.doneNavigatingToAlarmList()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_alarm_editor, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            viewModel.onActionSave()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    fun showTimeDialog() {
        viewModel.alarm.value?.let { alarm ->
            AlarmEditorTimeDialogFragment(viewModel.alarmEditorListener, alarm.timeMinutes).show(
                supportFragmentManager,
                "timeDialog"
            )
        }
    }

    fun onAlarmEditorPropertyClicked(view: View, alarmPropertyType: AlarmPropertyType) {
        when (alarmPropertyType) {
            AlarmPropertyType.NAME -> showNameDialog()
            AlarmPropertyType.SNOOZE_LENGTH -> showSnoozeLengthDialog()
            AlarmPropertyType.SOUND -> showRingtonePicker()
            AlarmPropertyType.VIBRATION_TYPE -> showVibrationTypePopupMenu(view)
        }
    }

    private fun showNameDialog() {
        viewModel.alarm.value?.let { alarm ->
            AlarmEditorNameDialogFragment(viewModel.alarmEditorListener, alarm.name).show(
                supportFragmentManager,
                "nameDialog"
            )
        }
    }

    private fun showSnoozeLengthDialog() {
        viewModel.alarm.value?.let { alarm ->
            AlarmEditorSnoozeLengthDialogFragment(
                viewModel.alarmEditorListener,
                alarm.snoozeLengthMinutes
            ).show(supportFragmentManager, "snoozeLengthDialog")
        }
    }

    private fun showRingtonePicker() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                resources.getString(R.string.alarm_editor_dialog_sound_title)
            )
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            if (Uri.EMPTY != viewModel.alarm.value?.soundUri) {
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    viewModel.alarm.value?.soundUri
                )
            }

        }
        startActivityForResult(intent, INTENT_RINGTONE_PICKER_REQUEST_CODE)
    }

    private fun showVibrationTypePopupMenu(view: View) {
        PopupMenu(context, view).apply {
            inflate(R.menu.menu_alarm_editor_property_vibration_type)
            setOnMenuItemClickListener(viewModel.alarmPropertyTypeVibrationTypePopupMenuListener)
            gravity = Gravity.END
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                INTENT_RINGTONE_PICKER_REQUEST_CODE -> {
                    data?.let {
                        val uri =
                            data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                        viewModel.alarmEditorListener.onRingtonePickerSaved(uri ?: "".toUri())
                    }
                }
            }
        }
    }


}