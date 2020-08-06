package com.example.alarmclock.alarm.alarmlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmclock.R
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

        viewModel = ViewModelProvider(this).get(AlarmListViewModel::class.java)

        val linearLayoutManager = LinearLayoutManager(this.context)
        binding.alarmList.layoutManager = linearLayoutManager

        val dividerItemDecoration =
            DividerItemDecoration(binding.alarmList.context, linearLayoutManager.orientation)
        binding.alarmList.addItemDecoration(dividerItemDecoration)

        val adapter = AlarmAdapter()
        binding.alarmList.adapter = adapter

        adapter.submitList(viewModel.alarmList)


        return binding.root
    }

}