package com.letsbuildthatapp.foodlocker.ui.registration

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity
import com.letsbuildthatapp.foodlocker.ui.activity.MainActivity
import com.letsbuildthatapp.foodlocker.ui.profile.settings.SettingsViewModel
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        if (requireActivity() is MainActivity) {
            val extraInfoRegistrationViewModel =
                    ViewModelProvider(requireActivity()).get(ExtraInfoRegistrationViewModel::class.java)
            extraInfoRegistrationViewModel.setDate("$day/$month/$year")
        }
        if (requireActivity() is LoggedActivity) {
            val settings =
                    ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
            settings.setDate("$day/$month/$year")
        }
    }
}
