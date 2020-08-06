package com.example.alarmclock.alarm.alarmlist

import com.example.alarmclock.dataclasses.Alarm
import java.time.DayOfWeek

class AlarmDataSource {

    //FIXME: Delete after either ability to add alarms or persistence

    companion object {
        fun createDataSet(): ArrayList<Alarm> {
            val list = ArrayList<Alarm>()
            list.add(Alarm(1, true, "Alarm 1", 60, listOf(DayOfWeek.MONDAY)))
            list.add(Alarm(2, false, "Alarm 2", 120, listOf(DayOfWeek.TUESDAY)))
            list.add(Alarm(3, true, "Alarm 3", 180, listOf(DayOfWeek.WEDNESDAY)))
            list.add(Alarm(4, false, "Alarm 4", 240, listOf(DayOfWeek.THURSDAY)))
            list.add(Alarm(5, true, "Alarm 5", 300, listOf(DayOfWeek.FRIDAY)))
            list.add(Alarm(6, false, "Alarm 6", 360, listOf(DayOfWeek.SATURDAY)))
            list.add(Alarm(7, true, "Alarm 7", 420, listOf(DayOfWeek.SUNDAY)))
            list.add(
                Alarm(
                    8, false, "Alarm 8", 90,
                    listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY
                    )
                )
            )
            list.add(Alarm(9, true, "Alarm 9", 210, listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)))
            list.add(
                Alarm(
                    10, false, "Alarm 10", 450, listOf(
                        DayOfWeek.MONDAY,
                        DayOfWeek.TUESDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY,
                        DayOfWeek.SUNDAY
                    )
                )
            )
            return list
        }


    }

}