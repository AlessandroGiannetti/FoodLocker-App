package com.letsbuildthatapp.foodlocker.ui.progress

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.letsbuildthatapp.foodlocker.databinding.FragmentProgressBinding
import com.letsbuildthatapp.foodlocker.utility.ApiStatus

private const val TAG = "PROGRESS FRAGMENT"

private lateinit var binding: FragmentProgressBinding
private var days = mutableMapOf<Float, String>()

class ProgressFragment : Fragment() {


    private val viewModel: ProgressViewModel by lazy {
        ViewModelProvider(this).get(ProgressViewModel::class.java)
    }

    private val entries = ArrayList<Entry>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        activity?.intent?.putExtra("frgToLoad", "PROGRESS_FRAGMENT")

        binding = FragmentProgressBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.weightList.adapter = WeightListAdapter()

        viewModel.properties.observe(viewLifecycleOwner, Observer { weightsList ->
            if (weightsList.isNotEmpty()) {
                plotGraph()
                binding.lineChart.visibility = View.VISIBLE
            }

        })

        binding.addWeight.setOnClickListener {
            val myIntent = Intent(activity, WriteWeightActivity::class.java)
            myIntent.putExtra("frgToLoad", "PROGRESS_FRAGMENT")
            this.startActivity(myIntent)
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


    private fun plotGraph() {

        //populate the entries
        for (item in viewModel.properties.value!!) {
            entries.add(Entry(item.id.toFloat(), item.weight.toFloat()))
            days[item.id.toFloat()] = item.createdAt.toString().substring(8, 10).plus("/").plus(item.createdAt.toString().substring(5, 7))
        }

        // reverse order !!important
        entries.reverse()

        val vl = LineDataSet(entries, "weight")

        //color - style settings
        vl.color = Color.RED
        vl.setCircleColor(Color.WHITE)
        vl.highLightColor = Color.WHITE
        vl.lineWidth = 3f
        vl.fillAlpha = Color.RED
        binding.lineChart.setBorderColor(Color.WHITE)
        binding.lineChart.xAxis.textColor = Color.WHITE
        binding.lineChart.axisLeft.textColor = Color.WHITE
        binding.lineChart.axisLeft.gridColor = Color.GRAY
        binding.lineChart.xAxis.axisLineWidth = 2f
        binding.lineChart.axisLeft.axisLineWidth = 2f
        binding.lineChart.axisLeft.gridLineWidth = 1f

        //miscellaneous
        vl.setDrawValues(false)
        vl.setDrawFilled(false)
        vl.isHighlightEnabled = true

        //personal formatting for data on the axis
        binding.lineChart.xAxis.valueFormatter = MyXAxisFormatter()
        binding.lineChart.axisLeft.valueFormatter = MyYAxisFormatter()

        //assign data to the chart
        binding.lineChart.data = LineData(vl)


        //axis settings
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.xAxis.axisMaximum++
        binding.lineChart.xAxis.granularity = 0.5f
        binding.lineChart.xAxis.textSize = 11f
        binding.lineChart.axisLeft.textSize = 12f
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //disabling line chart legend
        binding.lineChart.legend.isEnabled = false
        //interaction settings
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.setPinchZoom(false)

        //text description
        binding.lineChart.description.text = ""
        binding.lineChart.setNoDataText("Insert your weight")

        //update the chart
        binding.lineChart.invalidate()
    }
}


// data formatter for axis of the fraph
class MyXAxisFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return days.getOrDefault(value, "")
    }
}

class MyYAxisFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return value.toString().take(4).plus(" Kg")
    }

}


