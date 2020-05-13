package com.letsbuildthatapp.foodlocker.ui.searchUsers

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.ActivitySearchResultsBinding
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity
import com.letsbuildthatapp.foodlocker.utility.ApiStatus

private const val TAG = "SEARCH ACTIVITY"

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultsBinding

    private val viewModel: SearchResultsViewModel by lazy {
        ViewModelProvider(this).get(SearchResultsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_results)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.userList.adapter = SearchListAdapter()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onStart() {
        super.onStart()
        val query = this.intent?.extras?.getString("searchElement")
        viewModel.queryParameter = query.toString()
        this.title = "Results for: $query"
        Toast.makeText(this, "Results for: $query", Toast.LENGTH_SHORT).show()


        // Observer for the network error.
        viewModel.status.observe(this, Observer { isNetworkError ->
            if (isNetworkError == ApiStatus.ERROR) onNetworkError()
        })

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {

            android.R.id.home -> {
                val myIntent = Intent(this, LoggedActivity::class.java)
                // Log.e(TAG, "${this.intent?.extras?.getString("frgToLoad")}")
                myIntent.putExtra("frgToLoadFromActivity", this.intent?.extras?.getString("frgToLoad"))
                this.startActivity(myIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val myIntent = Intent(this, LoggedActivity::class.java)
        myIntent.putExtra("frgToLoadFromActivity", this.intent?.extras?.getString("frgToLoad"))
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        this.startActivity(myIntent)
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

}