package com.letsbuildthatapp.foodlocker.ui.profile.settings

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentSettingsBinding
import com.letsbuildthatapp.foodlocker.utility.ApiStatus


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var viewModel: SettingsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_settings,
                container,
                false)

        viewModel =
                ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)

        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        // specify the fragment view as the lifecycle owner of the binding
        //this is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                viewModel.genders)

        binding.genderExposedDropdown.setAdapter(adapter)

        viewModel.loadMyProfileImage(binding.imageviewProfile)

        // Observer for the network error.
        viewModel.status.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError == ApiStatus.ERROR) onNetworkError()
        })

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideKeyboard()

        val dateObserver = Observer<String> { newDate ->
            binding.dateBirthdayEdittextRegister.setText(newDate)
        }
        viewModel.dateOfBirthday.observe(requireActivity(), dateObserver)
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }


    // hide keyboard
    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


}
