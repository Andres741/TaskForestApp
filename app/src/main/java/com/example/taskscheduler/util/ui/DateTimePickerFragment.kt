package com.example.taskscheduler.util.ui

import androidx.fragment.app.FragmentManager
import com.example.taskscheduler.util.SimpleTimeDate
import com.example.taskscheduler.util.toSimpleTime
import com.example.taskscheduler.util.toSimpleTimeDate
import java.util.*

class DateTimePickerFragment(
    val calendar: () -> Calendar,
    val minDate: (() -> Calendar)? = null,
    val maxDate: (() -> Calendar)? = null,

    var is24HourView: Boolean,

    var listener: DateTimePickerClickListener,
) {
    fun show(manager: FragmentManager, tag: String?) {
        val datePickerFragment = DatePickerFragment(calendar(), minDate?.invoke(), maxDate?.invoke()) {  _, year, month, day ->

            val timePicker = TimePickerFragment(calendar().toSimpleTime(), is24HourView) { _, hour, minute ->

                val time = SimpleTimeDate(year, month, day, hour, minute)
                val minTime = minDate?.invoke()
                val maxTime = maxDate?.invoke()

                val selectedTime = minTime?.toSimpleTimeDate()?.takeIf { min ->
                    min >= time
                } ?: maxTime?.toSimpleTimeDate()?.takeIf { max ->
                    max <= time
                } ?: time

                listener.onClick(selectedTime)
            }
            timePicker.show(manager, tag)
        }
        datePickerFragment.show(manager, tag)
    }
    companion object {
        fun newInstanceMinNextMinute(is24HourView: Boolean, listener: DateTimePickerClickListener) = DateTimePickerFragment(
            calendar = { Calendar.getInstance() }, minDate = { Calendar.getInstance().apply { add(Calendar.MINUTE, 1) } },
            is24HourView = is24HourView, listener = listener,
        )
    }
}

fun interface DateTimePickerClickListener {
    fun onClick(simpleTimeDate: SimpleTimeDate)
}
