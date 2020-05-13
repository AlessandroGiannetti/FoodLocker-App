package com.letsbuildthatapp.foodlocker.ui.diary.addSport

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.ActivityAddSportBinding
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity

private const val TAG = "ADD SPORT ACTIVITY"


class AddSportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSportBinding

    private val viewModel: AddSportViewModel by lazy {
        ViewModelProvider(this).get(AddSportViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_sport)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    override fun onStart() {
        super.onStart()
        viewModel.getSportProperty(this.intent?.extras?.getString("sportId")!!)
        binding.minutesExercise.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val value = binding.minutesExercise.text.toString().toIntOrNull()
                if (value != null) {
                    viewModel.hour = binding.minutesExercise.text.toString()
                    viewModel.setCalories((viewModel.sport.value?.calories?.toInt()?.times(value))?.div(60)!!)
                } else
                    viewModel.setCalories(0)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_add_sport, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.add_sport -> {
                viewModel.setSport(this, this.intent?.extras?.getString("sportId")!!)
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


}
