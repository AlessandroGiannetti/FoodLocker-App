package com.letsbuildthatapp.foodlocker.ui.profile.followings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.databinding.FragmentFollowingBinding
import com.letsbuildthatapp.foodlocker.ui.profile.ProfileViewModel


class FollowingsFragment : Fragment() {

    private lateinit var binding: FragmentFollowingBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        activity?.intent?.putExtra("REFRESH RELATIONSHIPS", "FOLLOWINGS")

        binding = FragmentFollowingBinding.inflate(inflater)

        viewModel =
                ViewModelProvider(this).get(ProfileViewModel::class.java)


        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        binding.followingsList.adapter = FollowingsListAdapter()

        return binding.root
    }


}
