package com.letsbuildthatapp.foodlocker.ui.diary.searchSport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentSearchSportBinding

class SearchSportFragment : Fragment() {

    private lateinit var binding: FragmentSearchSportBinding

    private val viewModel: SearchSportViewModel by lazy {
        ViewModelProvider(this).get(SearchSportViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        activity?.intent?.putExtra("frgToLoad", "FOOD_SEARCH") // change

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_search_sport, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.sportListSearch.adapter = SportSearchListAdapter()

        viewModel.activity = requireActivity()

        return binding.root
    }

}
