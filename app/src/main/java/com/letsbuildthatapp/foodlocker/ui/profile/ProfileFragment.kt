package com.letsbuildthatapp.foodlocker.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentProfileBinding
import com.letsbuildthatapp.foodlocker.utility.ApiStatus

private const val TAG = "PROFILE FRAGMENT"

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    private val entries = ArrayList<Entry>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        activity?.intent?.putExtra("frgToLoad", "PROFILE_FRAGMENT")

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_profile, container, false)

        viewModel =
                ViewModelProvider(this).get(ProfileViewModel::class.java)


        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        viewModel.loadProfileImage(binding.imageviewProfile)

        viewModel.bmi.observe(viewLifecycleOwner, Observer { lastWeight ->
            if (lastWeight != null) {
                plotGraph()
            }
        })

        when (activity?.intent?.extras?.getString("REFRESH RELATIONSHIPS")) {
            "FOLLOWINGS" -> {
                viewModel.getFollowed()
                activity?.intent?.putExtra("REFRESH RELATIONSHIPS", "NONE")
            }
            "FOLLOWERS" -> {
                viewModel.getFollowers()
                activity?.intent?.putExtra("REFRESH RELATIONSHIPS", "NONE")
            }
        }

        binding.followerTitle.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_navigation_profile_to_followersFragment)
        }
        binding.followingTitle.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_navigation_profile_to_followingsFragment)
        }
        binding.itemMenuSettings.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_navigation_profile_to_settingsFragment)
        }
        binding.itemMenuMyProfile.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_navigation_profile_to_myProfileFragment)
        }
        binding.itemMenuNutrition.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_navigation_profile_to_nutritionFragment)
        }

        // Observer for the network error.
        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            if (status == ApiStatus.ERROR) onNetworkError()
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


    /**
     * Method for plot the graph of weights.
     */
    private fun plotGraph() {

        //populate the entries
        entries.add(Entry(viewModel.bmi.value?.toFloat()!!, 1f))

        val vl = ScatterDataSet(entries, "BMI")

        //color - style settings
        vl.highLightColor = Color.WHITE
        binding.scatterChartBMI.setBorderColor(Color.WHITE)
        binding.scatterChartBMI.xAxis.textColor = Color.WHITE
        binding.scatterChartBMI.xAxis.axisLineWidth = 2f
        binding.scatterChartBMI.xAxis.setDrawGridLines(false) // x axis grid lines

        binding.scatterChartBMI.xAxis.axisMinimum = 14f
        binding.scatterChartBMI.xAxis.axisMaximum = 43f
        binding.scatterChartBMI.axisLeft.axisMaximum = 4f

        binding.scatterChartBMI.axisLeft.setDrawLabels(false) // no axis labels
        binding.scatterChartBMI.axisLeft.setDrawAxisLine(false) // no axis line
        binding.scatterChartBMI.axisLeft.setDrawGridLines(false) // no grid lines
        binding.scatterChartBMI.axisLeft.setDrawZeroLine(false) // draw a zero line

        //miscellaneous
        vl.setDrawValues(false)
        vl.isHighlightEnabled = true
        vl.color = Color.WHITE
        vl.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
        vl.scatterShapeSize = 32f


        //assign data to the chart
        binding.scatterChartBMI.data = ScatterData(vl)


        //axis settings
        binding.scatterChartBMI.axisRight.isEnabled = false
        binding.scatterChartBMI.xAxis.granularity = 0.1f
        binding.scatterChartBMI.xAxis.textSize = 11f
        binding.scatterChartBMI.xAxis.position = XAxis.XAxisPosition.BOTTOM


        val l1 = LimitLine(16.4f, "16.4")
        val l2 = LimitLine(18.4f, "")
        val l22 = LimitLine(18.6f, "18.5")
        val l3 = LimitLine(24.8f, "")
        val l33 = LimitLine(25f, "25")
        val l4 = LimitLine(30f, "")
        val l44 = LimitLine(30.2f, "30")
        val l5 = LimitLine(35f, "35")
        val l6 = LimitLine(40f, "40")

        l1.lineColor = Color.BLUE
        l1.lineWidth = 2f
        l1.textColor = Color.WHITE
        l1.textSize = 10f
        binding.scatterChartBMI.xAxis.addLimitLine(l1)

        l2.lineColor = Color.BLUE
        l2.lineWidth = 2f
        binding.scatterChartBMI.xAxis.addLimitLine(l2)

        l22.lineColor = Color.GREEN
        l22.lineWidth = 2f
        l22.textColor = Color.WHITE
        l22.textSize = 10f
        binding.scatterChartBMI.xAxis.addLimitLine(l22)

        l3.lineColor = Color.GREEN
        l3.lineWidth = 2f
        binding.scatterChartBMI.xAxis.addLimitLine(l3)

        l33.lineColor = Color.YELLOW
        l33.lineWidth = 2f
        l33.textColor = Color.WHITE
        l33.textSize = 10f
        binding.scatterChartBMI.xAxis.addLimitLine(l33)

        l4.lineColor = Color.YELLOW
        l4.lineWidth = 2f
        binding.scatterChartBMI.xAxis.addLimitLine(l4)

        l44.lineColor = Color.RED
        l44.lineWidth = 2f
        l44.textColor = Color.WHITE
        l44.textSize = 10f
        binding.scatterChartBMI.xAxis.addLimitLine(l44)

        l5.lineColor = Color.RED
        l5.lineWidth = 2f
        l5.textColor = Color.WHITE
        l5.textSize = 10f
        binding.scatterChartBMI.xAxis.addLimitLine(l5)

        l6.lineColor = Color.RED
        l6.lineWidth = 2f
        l6.textColor = Color.WHITE
        l6.textSize = 10f
        binding.scatterChartBMI.xAxis.addLimitLine(l6)


        //disabling line chart legend
        binding.scatterChartBMI.legend.isEnabled = false
        //interaction settings
        binding.scatterChartBMI.setTouchEnabled(false)
        binding.scatterChartBMI.setPinchZoom(false)

        //text description
        binding.scatterChartBMI.description.text = ""
        binding.scatterChartBMI.setNoDataText("Insert your weight")

        //update the chart
        binding.scatterChartBMI.invalidate()
    }

}