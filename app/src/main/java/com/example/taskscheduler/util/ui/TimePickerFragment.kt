package com.example.taskscheduler.util.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.util.SimpleTime

class TimePickerFragment(
    var time: SimpleTime,
    var is24HourView: Boolean,

    var onClick: TimePickerDialog.OnTimeSetListener,
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(context, onClick, time.hour, time.minute, is24HourView)
    }
}
