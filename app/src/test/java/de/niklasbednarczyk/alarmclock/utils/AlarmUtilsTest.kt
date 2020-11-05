package de.niklasbednarczyk.alarmclock.utils

import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.time.DayOfWeek
import java.util.*

class AlarmUtilsTest {

    @Test
    fun hoursAndMinutesToTimeMinutes_min() {
        // GIVEN
        val given = Pair(0, 0)

        // WHEN
        val actual = hoursAndMinutesToTimeMinutes(given.first, given.second)

        // THEN
        assertThat(actual, `is`(0))
    }

    @Test
    fun hoursAndMinutesToTimeMinutes_inBetween() {
        // GIVEN
        val given = Pair(12, 30)

        // WHEN
        val actual = hoursAndMinutesToTimeMinutes(given.first, given.second)

        // THEN
        assertThat(actual, `is`(750))
    }

    @Test
    fun hoursAndMinutesToTimeMinutes_max() {
        // GIVEN
        val given = Pair(23, 59)

        // WHEN
        val actual = hoursAndMinutesToTimeMinutes(given.first, given.second)

        // THEN
        assertThat(actual, `is`(1439))
    }

    @Test
    fun timeMinutesToHoursAndMinutes_min() {
        // GIVEN
        val given = 0

        // WHEN
        val actual = timeMinutesToHoursAndMinutes(given)

        // THEN
        assertThat(actual.first, `is`(0))
        assertThat(actual.second, `is`(0))
    }

    @Test
    fun timeMinutesToHoursAndMinutes_inBetween() {
        // GIVEN
        val given = 750

        // WHEN
        val actual = timeMinutesToHoursAndMinutes(given)

        // THEN
        assertThat(actual.first, `is`(12))
        assertThat(actual.second, `is`(30))
    }

    @Test
    fun timeMinutesToHoursAndMinutes_max() {
        // GIVEN
        val given = 1439

        // WHEN
        val actual = timeMinutesToHoursAndMinutes(given)

        // THEN
        assertThat(actual.first, `is`(23))
        assertThat(actual.second, `is`(59))
    }

    @Test
    fun calendarToCalendarTimeMinutes_min() {
        // GIVEN
        val given = getCalendarFromHourAndMinute(0, 0)

        // WHEN
        val actual = calendarToCalendarTimeMinutes(given)

        // THEN
        assertThat(actual, `is`(0))
    }

    @Test
    fun calendarToCalendarTimeMinutes_inBetween() {
        // GIVEN
        val given = getCalendarFromHourAndMinute(12, 30)

        // WHEN
        val actual = calendarToCalendarTimeMinutes(given)

        // THEN
        assertThat(actual, `is`(750))
    }

    @Test
    fun calendarToCalendarTimeMinutes_max() {
        // GIVEN
        val given = getCalendarFromHourAndMinute(23, 59)

        // WHEN
        val actual = calendarToCalendarTimeMinutes(given)

        // THEN
        assertThat(actual, `is`(1439))
    }

    @Test
    fun calendarToCalendarHoursAndMinutes_min() {
        // GIVEN
        val given = getCalendarFromHourAndMinute(0, 0)

        // WHEN
        val actual = calendarToCalendarHoursAndMinutes(given)

        // THEN
        assertThat(actual.first, `is`(0))
        assertThat(actual.second, `is`(0))
    }

    @Test
    fun calendarToCalendarHoursAndMinutes_inBetween() {
        // GIVEN
        val given = getCalendarFromHourAndMinute(12, 30)

        // WHEN
        val actual = calendarToCalendarHoursAndMinutes(given)

        // THEN
        assertThat(actual.first, `is`(12))
        assertThat(actual.second, `is`(30))
    }

    @Test
    fun calendarToCalendarHoursAndMinutes_max() {
        // GIVEN
        val given = getCalendarFromHourAndMinute(23, 59)

        // WHEN
        val actual = calendarToCalendarHoursAndMinutes(given)

        // THEN
        assertThat(actual.first, `is`(23))
        assertThat(actual.second, `is`(59))
    }

    @Test
    fun timeMinutesToTimeMilliseconds_min() {
        // GIVEN
        val given = 0

        // WHEN
        val actual = timeMinutesToTimeMilliseconds(given)

        // THEN
        assertThat(actual, `is`(0))
    }

    @Test
    fun timeMinutesToTimeMilliseconds_inBetween() {
        // GIVEN
        val given = 15

        // WHEN
        val actual = timeMinutesToTimeMilliseconds(given)

        // THEN
        assertThat(actual, `is`(900000))
    }

    @Test
    fun timeMinutesToTimeMilliseconds_max() {
        // GIVEN
        val given = 30

        // WHEN
        val actual = timeMinutesToTimeMilliseconds(given)

        // THEN
        assertThat(actual, `is`(1800000))
    }

    @Test
    fun dayOfWeekToCalendarDay_sunday() {
        // GIVEN
        val given = DayOfWeek.SUNDAY

        // WHEN
        val actual = dayOfWeekToCalendarDay(given)

        // THEN
        assertThat(actual, `is`(1))
    }

    @Test
    fun dayOfWeekToCalendarDay_monday() {
        // GIVEN
        val given = DayOfWeek.MONDAY

        // WHEN
        val actual = dayOfWeekToCalendarDay(given)

        // THEN
        assertThat(actual, `is`(2))
    }

    @Test
    fun dayOfWeekToCalendarDay_tuesday() {
        // GIVEN
        val given = DayOfWeek.TUESDAY

        // WHEN
        val actual = dayOfWeekToCalendarDay(given)

        // THEN
        assertThat(actual, `is`(3))
    }

    @Test
    fun dayOfWeekToCalendarDay_wednesday() {
        // GIVEN
        val given = DayOfWeek.WEDNESDAY

        // WHEN
        val actual = dayOfWeekToCalendarDay(given)

        // THEN
        assertThat(actual, `is`(4))
    }

    @Test
    fun dayOfWeekToCalendarDay_thursday() {
        // GIVEN
        val given = DayOfWeek.THURSDAY

        // WHEN
        val actual = dayOfWeekToCalendarDay(given)

        // THEN
        assertThat(actual, `is`(5))
    }

    @Test
    fun dayOfWeekToCalendarDay_friday() {
        // GIVEN
        val given = DayOfWeek.FRIDAY

        // WHEN
        val actual = dayOfWeekToCalendarDay(given)

        // THEN
        assertThat(actual, `is`(6))
    }

    @Test
    fun dayOfWeekToCalendarDay_saturday() {
        // GIVEN
        val given = DayOfWeek.SATURDAY

        // WHEN
        val actual = dayOfWeekToCalendarDay(given)

        // THEN
        assertThat(actual, `is`(7))
    }

    @Test
    fun calendarToDayOfWeek_sunday() {
        // GIVEN
        val given = getCalendarFromDayOfWeek(1)

        // WHEN
        val actual = calendarToDayOfWeek(given)

        // THEN
        assertThat(actual, `is`(DayOfWeek.SUNDAY))
    }

    @Test
    fun calendarToDayOfWeek_monday() {
        // GIVEN
        val given = getCalendarFromDayOfWeek(2)

        // WHEN
        val actual = calendarToDayOfWeek(given)

        // THEN
        assertThat(actual, `is`(DayOfWeek.MONDAY))
    }

    @Test
    fun calendarToDayOfWeek_tuesday() {
        // GIVEN
        val given = getCalendarFromDayOfWeek(3)

        // WHEN
        val actual = calendarToDayOfWeek(given)

        // THEN
        assertThat(actual, `is`(DayOfWeek.TUESDAY))
    }

    @Test
    fun calendarToDayOfWeek_wednesday() {
        // GIVEN
        val given = getCalendarFromDayOfWeek(4)

        // WHEN
        val actual = calendarToDayOfWeek(given)

        // THEN
        assertThat(actual, `is`(DayOfWeek.WEDNESDAY))
    }

    @Test
    fun calendarToDayOfWeek_thursday() {
        // GIVEN
        val given = getCalendarFromDayOfWeek(5)

        // WHEN
        val actual = calendarToDayOfWeek(given)

        // THEN
        assertThat(actual, `is`(DayOfWeek.THURSDAY))
    }

    @Test
    fun calendarToDayOfWeek_friday() {
        // GIVEN
        val given = getCalendarFromDayOfWeek(6)

        // WHEN
        val actual = calendarToDayOfWeek(given)

        // THEN
        assertThat(actual, `is`(DayOfWeek.FRIDAY))
    }

    @Test
    fun calendarToDayOfWeek_saturday() {
        // GIVEN
        val given = getCalendarFromDayOfWeek(7)

        // WHEN
        val actual = calendarToDayOfWeek(given)

        // THEN
        assertThat(actual, `is`(DayOfWeek.SATURDAY))
    }

    @Test
    fun timeMinutesAndDaysToNextDay_today() {
        // GIVEN
        val givenTimeMinutes = Int.MAX_VALUE
        val givenDays = mutableListOf<DayOfWeek>()
        val expectedCalendar = getCalendarWithDaysAdded(0)

        // WHEN
        val actual = timeMinutesAndDaysToNextDay(givenTimeMinutes, givenDays)

        // THEN
        assertThat(actual, `is`(getDayOfWeekFromCalendar(expectedCalendar)))
    }

    @Test
    fun timeMinutesAndDaysToNextDay_tomorrow() {
        // GIVEN
        val givenTimeMinutes = Int.MIN_VALUE
        val givenDays = mutableListOf<DayOfWeek>()
        val expectedCalendar = getCalendarWithDaysAdded(1)

        // WHEN
        val actual = timeMinutesAndDaysToNextDay(givenTimeMinutes, givenDays)

        // THEN
        assertThat(actual, `is`(getDayOfWeekFromCalendar(expectedCalendar)))
    }

    @Test
    fun timeMinutesAndDaysToNextDay_sameDay() {
        // GIVEN
        val givenTimeMinutes = Int.MAX_VALUE
        val givenDays = EVERY_DAY.toMutableList()
        val expectedCalendar = getCalendarWithDaysAdded(0)

        // WHEN
        val actual = timeMinutesAndDaysToNextDay(givenTimeMinutes, givenDays)

        // THEN
        assertThat(actual, `is`(getDayOfWeekFromCalendar(expectedCalendar)))
    }

    @Test
    fun timeMinutesAndDaysToNextDay_nextDay() {
        // GIVEN
        val givenTimeMinutes = Int.MIN_VALUE
        val givenDays = EVERY_DAY.toMutableList()
        val expectedCalendar = getCalendarWithDaysAdded(1)

        // WHEN
        val actual = timeMinutesAndDaysToNextDay(givenTimeMinutes, givenDays)

        // THEN
        assertThat(actual, `is`(getDayOfWeekFromCalendar(expectedCalendar)))
    }

    private fun getCalendarFromHourAndMinute(hour: Int, minute: Int) =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

    private fun getCalendarFromDayOfWeek(dayOfWeek: Int) = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
    }

    private fun getCalendarWithDaysAdded(days: Int) = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_WEEK, days)
    }

    private fun getDayOfWeekFromCalendar(calendar: Calendar) = calendar.get(Calendar.DAY_OF_WEEK)
}