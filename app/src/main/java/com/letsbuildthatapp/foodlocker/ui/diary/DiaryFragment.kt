package com.letsbuildthatapp.foodlocker.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentDiaryBinding
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "DIARY FRAGMENT"

class DiaryFragment : Fragment() {

    private val todayDate = LocalDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ISO_DATE).toString()
    private val formatterData: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private lateinit var binding: FragmentDiaryBinding
    private val viewModel: DiaryViewModel by lazy {
        ViewModelProvider(this).get(DiaryViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        activity?.intent?.putExtra("frgToLoad", "DIARY_FRAGMENT")

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_diary, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.foodListBreakfast.adapter = FoodListAdapter()
        binding.foodListLunch.adapter = FoodListAdapter()
        binding.foodListDinner.adapter = FoodListAdapter()
        binding.foodListSnack.adapter = FoodListAdapter()
        binding.exerciseList.adapter = ExerciseListAdapter()

        // macro click management
        binding.cardProgress.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_navigation_diary_to_Nutritions, bundleOf("daySelected" to viewModel.day.value?.date.toString()))
        }

        // day click management
        binding.dayBack.setOnClickListener {
            val currentDate = LocalDate.parse(viewModel.day.value?.date, formatterData).plusDays(-1).toString()

            viewModel.foodsByMeal = hashMapOf(1 to mutableListOf(), 2 to mutableListOf(), 3 to mutableListOf(), 4 to mutableListOf(), -1 to mutableListOf())
            viewModel.getDayProperties(currentDate)

            /* if (currentDate == todayDate)
                 showButtonsDiary()
             else
                 hideButtonsDiary()*/
        }
        binding.dayFw.setOnClickListener {
            val currentDate = LocalDate.parse(viewModel.day.value?.date, formatterData).plusDays(1).toString()

            viewModel.foodsByMeal = hashMapOf(1 to mutableListOf(), 2 to mutableListOf(), 3 to mutableListOf(), 4 to mutableListOf(), -1 to mutableListOf())
            viewModel.getDayProperties(currentDate)

            /*      if (currentDate == todayDate)
                      showButtonsDiary()
                  else
                      hideButtonsDiary()*/
        }

        // Observer for the network error.
        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            if (status == ApiStatus.ERROR) onNetworkError()
        })

        return binding.root
    }

    private fun hideButtonsDiary() {
        binding.addFoodBreakfast.visibility = View.INVISIBLE
        binding.addFoodDinner.visibility = View.INVISIBLE
        binding.addFoodLunch.visibility = View.INVISIBLE
        binding.addFoodSnack.visibility = View.INVISIBLE
        binding.addExercise.visibility = View.INVISIBLE
    }

    private fun showButtonsDiary() {
        binding.addFoodBreakfast.visibility = View.VISIBLE
        binding.addFoodDinner.visibility = View.VISIBLE
        binding.addFoodLunch.visibility = View.VISIBLE
        binding.addFoodSnack.visibility = View.VISIBLE
        binding.addExercise.visibility = View.VISIBLE
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
