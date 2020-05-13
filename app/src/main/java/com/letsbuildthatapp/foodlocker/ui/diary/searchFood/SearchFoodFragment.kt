package com.letsbuildthatapp.foodlocker.ui.diary.searchFood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentSearchFoodBinding

class SearchFoodFragment : Fragment() {

    private lateinit var binding: FragmentSearchFoodBinding

    private val viewModel: SearchFoodViewModel by lazy {
        ViewModelProvider(this).get(SearchFoodViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        activity?.intent?.putExtra("frgToLoad", "FOOD_SEARCH")

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_search_food, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.foodListSearch.adapter = FoodSearchListAdapter(arguments?.getString("mealType")!!)

        viewModel.activity = requireActivity()
        return binding.root
    }

}
