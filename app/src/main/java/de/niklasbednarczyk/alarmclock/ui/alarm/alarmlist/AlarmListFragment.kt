package de.niklasbednarczyk.alarmclock.ui.alarm.alarmlist

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.niklasbednarczyk.alarmclock.R
import de.niklasbednarczyk.alarmclock.databinding.FragmentAlarmListBinding
import de.niklasbednarczyk.alarmclock.enums.AlarmType
import de.niklasbednarczyk.alarmclock.utils.cancelAlarm
import de.niklasbednarczyk.alarmclock.utils.setNormalAlarm
import de.niklasbednarczyk.alarmclock.utils.setPreviewAlarm

@AndroidEntryPoint
class AlarmListFragment : Fragment() {

    private val viewModel by viewModels<AlarmListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAlarmListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_alarm_list,
            container,
            false
        )

        setHasOptionsMenu(true)

        binding.lifecycleOwner = this

        binding.alarmListViewModel = viewModel

        val linearLayoutManager = LinearLayoutManager(this.context)
        binding.alarmListRecyclerView.layoutManager = linearLayoutManager

        val onItemClickListener = viewModel.alarmOnItemClickListener
        val adapter = AlarmListAdapter(onItemClickListener)
        binding.alarmListRecyclerView.adapter = adapter

        val spacePixels = resources.getDimensionPixelSize(R.dimen.margin_layout_small)
        val alarmListItemDecoration = AlarmListItemDecoration(spacePixels)
        binding.alarmListRecyclerView.addItemDecoration(alarmListItemDecoration)

        viewModel.alarms.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAlarmEditor.observe(viewLifecycleOwner, { alarmId ->
            alarmId?.let {
                this.findNavController().navigate(
                    AlarmListFragmentDirections.actionAlarmListFragmentToAlarmEditorFragment(it)
                )
                viewModel.doneNavigatingToAlarmEditor()
            }
        })

        viewModel.navigateToSettings.observe(viewLifecycleOwner, { navigate ->
            navigate?.let {
                this.findNavController().navigate(
                    AlarmListFragmentDirections.actionAlarmListFragmentToSettingsFragment()
                )
                viewModel.doneNavigatingToSettings()
            }
        })

        viewModel.showItemPopUpMenu.observe(viewLifecycleOwner, { (view, _) ->
            view?.let { v ->
                PopupMenu(context, v).apply {
                    setOnMenuItemClickListener(viewModel.alarmItemPopupMenuListener)
                    inflate(R.menu.menu_alarm_list_item)
                    show()
                }
                viewModel.doneShowingItemPopUpMenu()
            }
        })

        viewModel.eventDeleteAlarm.observe(viewLifecycleOwner, { alarm ->
            alarm?.let {
                viewModel.startCancellingAlarm(alarm)

                view?.let { view ->
                    val alarmName =
                        if (!alarm.name.isBlank()) alarm.name else resources.getString(R.string.alarm)
                    val text =
                        String.format(
                            resources.getString(R.string.alarm_list_fragment_delete_snackbar_text),
                            alarmName
                        )
                    Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                        .setAction(R.string.alarm_list_fragment_delete_snackbar_action) {
                            viewModel.insertAlarm(alarm)
                        }.show()
                }

                viewModel.doneDeletingAlarm()
            }
        })

        viewModel.eventSetNormalAlarm.observe(viewLifecycleOwner, { alarm ->
            alarm?.let {
                if (alarm.isActive) {
                    setNormalAlarm(context, alarm)
                }
                viewModel.doneSettingNormalAlarm()
            }
        })

        viewModel.eventSetPreviewAlarm.observe(viewLifecycleOwner, { alarm ->
            alarm?.let {
                setPreviewAlarm(context, alarm)
                viewModel.doneSettingPreviewAlarm()
            }
        })

        viewModel.eventCancelAlarm.observe(viewLifecycleOwner, { alarm ->
            alarm?.let {
                AlarmType.values().forEach { type ->
                    if (type != AlarmType.NONE) {
                        cancelAlarm(context, alarm, type)
                    }
                }
                viewModel.doneCancellingAlarm()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_alarm_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            viewModel.startNavigatingToSettings()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}