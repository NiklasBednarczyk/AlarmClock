package com.example.alarmclock.screens.alarm.alarmlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmClockDatabase
import com.example.alarmclock.databinding.FragmentAlarmListBinding

class AlarmListFragment : Fragment() {

    private lateinit var viewModel: AlarmListViewModel

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

        binding.lifecycleOwner = this

        val application = requireActivity().application

        val dao = AlarmClockDatabase.getInstance(application).alarmDao

        val viewModelFactory = AlarmListViewModelFactory(dao)

        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmListViewModel::class.java)
        binding.alarmListViewModel = viewModel

        val linearLayoutManager = LinearLayoutManager(this.context)
        binding.alarmList.layoutManager = linearLayoutManager

        val dividerItemDecoration =
            DividerItemDecoration(binding.alarmList.context, linearLayoutManager.orientation)
        binding.alarmList.addItemDecoration(dividerItemDecoration)

        val onItemClickListener = viewModel.alarmOnItemClickListener
        val adapter = AlarmAdapter(onItemClickListener)
        binding.alarmList.adapter = adapter

        viewModel.alarms.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAlarmEditor.observe(viewLifecycleOwner, Observer { alarmId ->
            alarmId?.let {
                this.findNavController().navigate(
                    AlarmListFragmentDirections.actionAlarmListFragmentToAlarmEditorFragment(it)
                )
                viewModel.doneNavigatingToAlarmEditor()
            }
        })

        viewModel.showItemPopUpMenu.observe(viewLifecycleOwner, Observer { (view, _) ->
            view?.let { v ->
                PopupMenu(context, v).apply {
                    setOnMenuItemClickListener(viewModel.alarmItemPopupMenuListener)
                    inflate(R.menu.menu_alarm_list_item)
                    show()
                }
                viewModel.doneShowingItemPopUpMenu()
            }
        })

        return binding.root
    }

}