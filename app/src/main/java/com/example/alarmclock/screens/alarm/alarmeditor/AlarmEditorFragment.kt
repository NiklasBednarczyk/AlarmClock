package com.example.alarmclock.screens.alarm.alarmeditor

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmClockDatabase
import com.example.alarmclock.databinding.FragmentAlarmEditorBinding
import com.example.alarmclock.screens.alarm.alarmeditor.dialogs.AlarmEditorNameDialogFragment
import com.example.alarmclock.screens.alarm.alarmeditor.dialogs.AlarmEditorTimeDialogFragment
import com.example.alarmclock.utils.setNormalAlarm

class AlarmEditorFragment : Fragment() {

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

        val args = AlarmEditorFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = AlarmEditorViewModelFactory(dao, args.alarmId)
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

    fun showNameDialog() {
        viewModel.alarm.value?.let { alarm ->
            AlarmEditorNameDialogFragment(viewModel.alarmEditorListener, alarm.name).show(
                supportFragmentManager,
                "nameDialog"
            )
        }
    }

}