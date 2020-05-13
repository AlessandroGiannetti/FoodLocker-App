package com.letsbuildthatapp.foodlocker.ui.diary.addFood

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.ActivityAddFoodBinding
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity


class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding

    private val viewModel: AddFoodViewModel by lazy {
        ViewModelProvider(this).get(AddFoodViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_food)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onStart() {
        super.onStart()
        viewModel.getFoodProperty(this.intent?.extras?.getString("foodId")!!)

        viewModel.food.observe(this, Observer { food ->
            if (food != null) {
                plotGraph()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_add_food, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add_food -> {
                viewModel.setFood(this, this.intent?.extras?.getString("foodId")!!, this.intent?.extras?.getString("mealType")!!)
            }
            android.R.id.home -> {
                val myIntent = Intent(this, LoggedActivity::class.java)
                myIntent.putExtra("frgToLoadFromActivity", "DIARY_FRAGMENT")
                this.startActivity(myIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val myIntent = Intent(this, LoggedActivity::class.java)
        myIntent.putExtra("frgToLoadFromActivity", "DIARY_FRAGMENT")
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this.startActivity(myIntent)
    }

    private fun plotGraph() {
        var fat = viewModel.food.value?.lipid_tot_g?.toFloatOrNull()
        if (fat == null) fat = 0.0f
        var carb = viewModel.food.value?.carbohydrt_g?.toFloatOrNull()
        if (carb == null) carb = 0.0f
        var pro = viewModel.food.value?.protein_g?.toFloatOrNull()
        if (pro == null) pro = 0.0f

        val tot = fat + carb + pro
        val percFat = fat.div(tot).times(100f)
        val percCarb = carb.div(tot).times(100f)
        val percPro = pro.div(tot).times(100f)

        viewModel.setPercFat(percFat.toString().take(4) + "%")
        viewModel.setPercCarb(percCarb.toString().take(4) + "%")
        viewModel.setPercPro(percPro.toString().take(4) + "%")

        val entries = ArrayList<PieEntry>()

        entries.add(PieEntry(percCarb, ""))
        entries.add(PieEntry(percFat, ""))
        entries.add(PieEntry(percPro, ""))
        val set = PieDataSet(entries, "")
        set.setColors(intArrayOf(R.color.carbo, R.color.fat, R.color.proteins), this)
        //disable values on chart
        set.setDrawValues(false)
        //associate data with graph
        val data = PieData(set)

        binding.pieChart.setDrawRoundedSlices(true)

        // piechart center text
        binding.pieChart.centerText = viewModel.food.value?.energy_kcal + "\nKcal"
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

    }

}
