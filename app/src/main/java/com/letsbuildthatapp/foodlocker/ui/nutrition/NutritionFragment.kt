package com.letsbuildthatapp.foodlocker.ui.nutrition

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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentNutritionBinding
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class NutritionFragment : Fragment() {

    private lateinit var binding: FragmentNutritionBinding
    private val formatterData: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val viewModel: NutritionViewModel by lazy {
        ViewModelProvider(this).get(NutritionViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_nutrition, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        if (!arguments?.getString("daySelected").isNullOrEmpty())
            viewModel.getDayProperties(arguments?.getString("daySelected")!!)
        else
            viewModel.getDayProperties(LocalDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ISO_DATE))

        // day click management
        binding.dayBack.setOnClickListener {
            viewModel.nutritionPerMeal = hashMapOf(1 to mutableListOf(0f, 0f, 0f, 0f), 2 to mutableListOf(0f, 0f, 0f, 0f), 3 to mutableListOf(0f, 0f, 0f, 0f), 4 to mutableListOf(0f, 0f, 0f, 0f), 5 to mutableListOf(0f, 0f, 0f, 0f))
            viewModel.getDayProperties(LocalDate.parse(viewModel.day.value?.date, formatterData).plusDays(-1).toString())
        }
        binding.dayFw.setOnClickListener {
            viewModel.nutritionPerMeal = hashMapOf(1 to mutableListOf(0f, 0f, 0f, 0f), 2 to mutableListOf(0f, 0f, 0f, 0f), 3 to mutableListOf(0f, 0f, 0f, 0f), 4 to mutableListOf(0f, 0f, 0f, 0f), 5 to mutableListOf(0f, 0f, 0f, 0f))
            viewModel.getDayProperties(LocalDate.parse(viewModel.day.value?.date, formatterData).plusDays(1).toString())
        }

        // Observer for the network error.
        viewModel.status.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError == ApiStatus.ERROR) onNetworkError()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.done.observe(viewLifecycleOwner, Observer { workDone ->
            if (workDone) {
                plotGraph()
            }
        })

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

        val totMacro = viewModel.nutritionPerMeal[5]!![1] + viewModel.nutritionPerMeal[5]!![2] + viewModel.nutritionPerMeal[5]!![3]
        val totCarb = viewModel.nutritionPerMeal[5]!![1]
        val totFat = viewModel.nutritionPerMeal[5]!![2]
        val totPro = viewModel.nutritionPerMeal[5]!![3]


        // all day macro
        var percCarbAllDay = viewModel.nutritionPerMeal[5]!![1].div(totMacro).times(100f)
        if (percCarbAllDay.isNaN()) percCarbAllDay = 0f
        var percFatAllDay = viewModel.nutritionPerMeal[5]!![2].div(totMacro).times(100f)
        if (percFatAllDay.isNaN()) percFatAllDay = 0f
        var percProAllDay = viewModel.nutritionPerMeal[5]!![3].div(totMacro).times(100f)
        if (percProAllDay.isNaN()) percProAllDay = 0f

        // carb %
        var percCarbB = viewModel.nutritionPerMeal[1]!![1].div(totCarb).times(100f)
        if (percCarbB.isNaN()) percCarbB = 0f
        var percCarbL = viewModel.nutritionPerMeal[2]!![1].div(totCarb).times(100f)
        if (percCarbL.isNaN()) percCarbL = 0f
        var percCarbD = viewModel.nutritionPerMeal[3]!![1].div(totCarb).times(100f)
        if (percCarbD.isNaN()) percCarbD = 0f
        var percCarbS = viewModel.nutritionPerMeal[4]!![1].div(totCarb).times(100f)
        if (percCarbS.isNaN()) percCarbS = 0f

        // fats %
        var percFatB = viewModel.nutritionPerMeal[1]!![2].div(totFat).times(100f)
        if (percFatB.isNaN()) percFatB = 0f
        var percFatL = viewModel.nutritionPerMeal[2]!![2].div(totFat).times(100f)
        if (percFatL.isNaN()) percFatL = 0f
        var percFatD = viewModel.nutritionPerMeal[3]!![2].div(totFat).times(100f)
        if (percFatD.isNaN()) percFatD = 0f
        var percFatS = viewModel.nutritionPerMeal[4]!![2].div(totFat).times(100f)
        if (percFatS.isNaN()) percFatS = 0f

        // pro %
        var percProB = viewModel.nutritionPerMeal[1]!![3].div(totPro).times(100f)
        if (percProB.isNaN()) percProB = 0f
        var percProL = viewModel.nutritionPerMeal[2]!![3].div(totPro).times(100f)
        if (percProL.isNaN()) percProL = 0f
        var percProD = viewModel.nutritionPerMeal[3]!![3].div(totPro).times(100f)
        if (percProD.isNaN()) percProD = 0f
        var percProS = viewModel.nutritionPerMeal[4]!![3].div(totPro).times(100f)
        if (percProS.isNaN()) percProS = 0f


        // bind data on layout (g and %) all day
        binding.carbohydratesPerc.text = percCarbAllDay.toString().take(4).plus("%")
        binding.carbohydratesValG.text = viewModel.nutritionPerMeal[5]!![1].toString().take(5)

        binding.fatsPerc.text = percFatAllDay.toString().take(4).plus("%")
        binding.fatsValG.text = viewModel.nutritionPerMeal[5]!![2].toString().take(5)

        binding.proteinsPerc.text = percProAllDay.toString().take(4).plus("%")
        binding.proteinsValG.text = viewModel.nutritionPerMeal[5]!![3].toString().take(5)

        //bind data on layout (g and %) carb
        binding.valBCarbG.text = viewModel.nutritionPerMeal[1]!![1].toString().take(5).plus("g")
        binding.valBCarbPerc.text = percCarbB.toString().take(4).plus("%")

        binding.valLCarbG.text = viewModel.nutritionPerMeal[2]!![1].toString().take(5).plus("g")
        binding.valLCarbPerc.text = percCarbL.toString().take(4).plus("%")

        binding.valDCarbG.text = viewModel.nutritionPerMeal[3]!![1].toString().take(5).plus("g")
        binding.valDCarbPerc.text = percCarbD.toString().take(4).plus("%")

        binding.valSCarbG.text = viewModel.nutritionPerMeal[4]!![1].toString().take(5).plus("g")
        binding.valSCarbPerc.text = percCarbS.toString().take(4).plus("%")

        //bind data on layout (g and %) fat
        binding.valBFatG.text = viewModel.nutritionPerMeal[1]!![2].toString().take(5).plus("g")
        binding.valBFatPerc.text = percFatB.toString().take(4).plus("%")

        binding.valLFatG.text = viewModel.nutritionPerMeal[2]!![2].toString().take(5).plus("g")
        binding.valLFatPerc.text = percFatL.toString().take(4).plus("%")

        binding.valDFatG.text = viewModel.nutritionPerMeal[3]!![2].toString().take(5).plus("g")
        binding.valDFatPerc.text = percFatD.toString().take(4).plus("%")

        binding.valSFatG.text = viewModel.nutritionPerMeal[4]!![2].toString().take(5).plus("g")
        binding.valSFatPerc.text = percFatS.toString().take(4).plus("%")

        //bind data on layout (g and %) pro
        binding.valBProG.text = viewModel.nutritionPerMeal[1]!![3].toString().take(5).plus("g")
        binding.valBProPerc.text = percProB.toString().take(4).plus("%")

        binding.valLProG.text = viewModel.nutritionPerMeal[2]!![3].toString().take(5).plus("g")
        binding.valLProPerc.text = percProL.toString().take(4).plus("%")

        binding.valDProG.text = viewModel.nutritionPerMeal[3]!![3].toString().take(5).plus("g")
        binding.valDProPerc.text = percProD.toString().take(4).plus("%")

        binding.valSProG.text = viewModel.nutritionPerMeal[4]!![3].toString().take(5).plus("g")
        binding.valSProPerc.text = percProS.toString().take(4).plus("%")


        // all day graph
        val allDay = ArrayList<PieEntry>()
        allDay.add(PieEntry(percCarbAllDay, ""))
        allDay.add(PieEntry(percFatAllDay, ""))
        allDay.add(PieEntry(percProAllDay, ""))
        val set = PieDataSet(allDay, "")
        set.setColors(intArrayOf(R.color.carbo, R.color.fat, R.color.proteins), context)
        //disable values on chart
        set.setDrawValues(false)
        //associate data with graph
        val data = PieData(set)

        binding.pieChart.setDrawRoundedSlices(true)

        // piechart center text
        binding.pieChart.centerText = viewModel.nutritionPerMeal[5]!![0].toString() + "\nKcal"
        binding.pieChart.setCenterTextSize(15f)

        // appearance piechart
        binding.pieChart.holeRadius = 88f
        binding.pieChart.setCenterTextColor(Color.WHITE)
        binding.pieChart.center
        binding.pieChart.setHoleColor(0)

        // legend disabled
        binding.pieChart.description.text = ""
        binding.pieChart.legend.isEnabled = false
        binding.pieChart.animateX(400)

        //data binding
        binding.pieChart.data = data

        //interaction
        binding.pieChart.setTouchEnabled(true)
        binding.pieChart.invalidate() // refresh


        //carb
        val carbEntries = ArrayList<PieEntry>()
        carbEntries.add(PieEntry(percCarbB, ""))
        carbEntries.add(PieEntry(percCarbL, ""))
        carbEntries.add(PieEntry(percCarbD, ""))
        carbEntries.add(PieEntry(percCarbS, ""))
        val setCarb = PieDataSet(carbEntries, "")
        setCarb.setColors(intArrayOf(R.color.breakfast, R.color.lunch, R.color.dinner, R.color.snack), context)
        //disable values on chart
        setCarb.setDrawValues(false)
        //associate data with graph
        val dataCarb = PieData(setCarb)

        binding.pieChartCarbo.setDrawRoundedSlices(true)

        // piechart center text
        binding.pieChartCarbo.centerText = totCarb.toString().take(6) + "\ng"
        binding.pieChartCarbo.setCenterTextSize(15f)

        // appearance piechart
        binding.pieChartCarbo.holeRadius = 88f
        binding.pieChartCarbo.setCenterTextColor(Color.WHITE)
        binding.pieChartCarbo.center
        binding.pieChartCarbo.setHoleColor(0)

        // legend disabled
        binding.pieChartCarbo.description.text = ""
        binding.pieChartCarbo.legend.isEnabled = false
        binding.pieChartCarbo.animateX(600)

        //data binding
        binding.pieChartCarbo.data = dataCarb

        //interaction
        binding.pieChartCarbo.setTouchEnabled(false)
        binding.pieChartCarbo.invalidate() // refresh

        //fat
        val fatEntries = ArrayList<PieEntry>()
        fatEntries.add(PieEntry(percFatB, ""))
        fatEntries.add(PieEntry(percFatL, ""))
        fatEntries.add(PieEntry(percFatD, ""))
        fatEntries.add(PieEntry(percFatS, ""))
        val setFat = PieDataSet(fatEntries, "")
        setFat.setColors(intArrayOf(R.color.breakfast, R.color.lunch, R.color.dinner, R.color.snack), context)
        //disable values on chart
        setFat.setDrawValues(false)
        //associate data with graph
        val dataFat = PieData(setFat)

        binding.pieChartFats.setDrawRoundedSlices(true)

        // piechart center text
        binding.pieChartFats.centerText = totFat.toString().take(6) + "\ng"
        binding.pieChartFats.setCenterTextSize(15f)

        // appearance piechart
        binding.pieChartFats.holeRadius = 88f
        binding.pieChartFats.setCenterTextColor(Color.WHITE)
        binding.pieChartFats.center
        binding.pieChartFats.setHoleColor(0)

        // legend disabled
        binding.pieChartFats.description.text = ""
        binding.pieChartFats.legend.isEnabled = false
        binding.pieChartFats.animateX(800)

        //data binding
        binding.pieChartFats.data = dataFat

        //interaction
        binding.pieChartFats.setTouchEnabled(false)

        binding.pieChartFats.invalidate() // refresh

        //pro
        val proEntries = ArrayList<PieEntry>()
        proEntries.add(PieEntry(percProB, ""))
        proEntries.add(PieEntry(percProL, ""))
        proEntries.add(PieEntry(percProD, ""))
        proEntries.add(PieEntry(percProS, ""))
        val setPro = PieDataSet(proEntries, "")
        setPro.setColors(intArrayOf(R.color.breakfast, R.color.lunch, R.color.dinner, R.color.snack), context)
        //disable values on chart
        setPro.setDrawValues(false)
        //associate data with graph
        val dataPro = PieData(setPro)

        binding.pieChartPros.setDrawRoundedSlices(true)

        // piechart center text
        binding.pieChartPros.centerText = totPro.toString().take(6) + "\ng"
        binding.pieChartPros.setCenterTextSize(15f)

        // appearance piechart
        binding.pieChartPros.holeRadius = 88f
        binding.pieChartPros.setCenterTextColor(Color.WHITE)
        binding.pieChartPros.center
        binding.pieChartPros.setHoleColor(0)


        // legend disabled
        binding.pieChartPros.description.text = ""
        binding.pieChartPros.legend.isEnabled = false
        binding.pieChartPros.animateX(1000)

        //data binding
        binding.pieChartPros.data = dataPro

        //interaction
        binding.pieChartPros.setTouchEnabled(false)

        binding.pieChartPros.invalidate() // refresh
    }
}
