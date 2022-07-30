package com.example.taskscheduler.util.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment (
    val calendar: Calendar,
    val minDate: Calendar? = null,
    val maxDate: Calendar? = null,
    var listener: DatePickerDialog.OnDateSetListener,
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity!!, listener, year, month, day).also { dialog ->
            if (minDate != null)
                dialog.datePicker.minDate = minDate.timeInMillis

            if (maxDate != null)
                dialog.datePicker.maxDate = maxDate.timeInMillis
        }
    }

    companion object {
        fun newInstanceMinTomorrow(listener: DatePickerDialog.OnDateSetListener): DatePickerFragment {
            val nowDate = Calendar.getInstance()
            val tomorrowDate = (nowDate.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, 1)
            }
            return DatePickerFragment(nowDate, tomorrowDate, null, listener)
        }
    }
}
