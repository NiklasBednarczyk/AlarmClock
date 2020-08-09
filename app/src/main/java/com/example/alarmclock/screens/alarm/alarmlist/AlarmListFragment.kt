package com.example.alarmclock.screens.alarm.alarmlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmclock.R
import com.example.alarmclock.database.AlarmClockDatabase
import com.example.alarmclock.databinding.FragmentAlarmListBinding

class AlarmListFragment : Fragment() {

    private lateinit var viewModel: AlarmListViewModel

    private lateinit var binding: FragmentAlarmListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_alarm_list,
            container,
            false
        )

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application

        val dao = AlarmClockDatabase.getInstance(application).alarmDao

        val viewModelFactory = AlarmListViewModelFactory(dao)

        viewModel = ViewModelProvider(this, viewModelFactory).get(AlarmListViewModel::class.java)

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


        return binding.root
    }

}