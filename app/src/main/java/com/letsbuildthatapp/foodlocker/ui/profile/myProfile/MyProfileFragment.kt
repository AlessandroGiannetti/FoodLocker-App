package com.letsbuildthatapp.foodlocker.ui.profile.myProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentMyProfileBinding
import com.letsbuildthatapp.foodlocker.utility.ApiStatus

private const val TAG = "PROFILE FRAGMENT"

class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var viewModel: MyProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        activity?.intent?.putExtra("frgToLoad", "PROFILE_FRAGMENT")

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_my_profile, container, false)

        viewModel =
                ViewModelProvider(this).get(MyProfileViewModel::class.java)


        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        viewModel.loadMyProfileImage(binding.imageviewProfile)

        binding.postList.adapter = MyProfilePostListAdapter()

        // Observer for the network error.
        viewModel.status.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError == ApiStatus.ERROR) onNetworkError()
        })

        return binding.root
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

}
