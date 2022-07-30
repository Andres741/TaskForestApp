package com.example.taskscheduler.util.ui

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import com.example.taskscheduler.util.SimpleTime
import java.text.DateFormat
import java.util.*


class RangeTimePickerDialog(
    context: Context?,
    callBack: OnTimeSetListener?,

    private var time: SimpleTime,
    var maxTime: SimpleTime = SimpleTime(-1, -1),
    var minTime: SimpleTime = SimpleTime(25, -25),

    is24HourView: Boolean

) : TimePickerDialog(context, callBack, time.hour, time.minute, is24HourView) {

    init {
        try {
            val superclass: Class<*> = javaClass.superclass as Class<*>
            val mTimePickerField = superclass.getDeclaredField("mTimePicker")
            mTimePickerField.isAccessible = true
            val mTimePicker = mTimePickerField[this] as TimePicker
            mTimePicker.setOnTimeChangedListener(this)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }
    }


    private val calendar = Calendar.getInstance()
    private val dateFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onTimeChanged(view: TimePicker, newHour: Int, newMinute: Int) {
        var validTime = true

        val (currentMinute, currentHour) = time
        val (minMinute, minHour) = minTime
        val (maxMinute, maxHour) = maxTime

        if (newHour < minHour || newHour == minHour && newMinute < minMinute) {
            validTime = false
        }
        if (newHour > maxHour || newHour == maxHour && newMinute > maxMinute) {
            validTime = false
        }
        if (validTime) {
            time = SimpleTime(newHour, newMinute)
        }
        updateTime(currentHour, currentMinute)  // produces stack overflow
        updateDialogTitle(currentHour, currentMinute)
    }

    private fun updateDialogTitle(hourOfDay: Int, minute: Int) {
        calendar[Calendar.HOUR_OF_DAY] = hourOfDay
        calendar[Calendar.MINUTE] = minute
        val title = dateFormat.format(calendar.time)
        setTitle(title)
    }
}
