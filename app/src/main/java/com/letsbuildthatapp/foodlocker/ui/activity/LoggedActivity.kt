package com.letsbuildthatapp.foodlocker.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.firebase.auth.FirebaseAuth
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.database.getDatabase
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.FirebaseUserLiveData
import com.letsbuildthatapp.foodlocker.repository.PostsRepository
import com.letsbuildthatapp.foodlocker.ui.messages.MessagesActivity
import com.letsbuildthatapp.foodlocker.ui.registration.DatePickerFragment
import com.letsbuildthatapp.foodlocker.ui.searchUsers.SearchResultsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "LOGGED ACTIVITY"

class LoggedActivity : AppCompatActivity() {

    private val activityScopeJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val activityScope = CoroutineScope(
            activityScopeJob + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //observe if a user is logged or not
        FirebaseUserLiveData().observe(this, Observer { authenticationState ->
            if (authenticationState == null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                // initialization of user info (Firebase realtime database)
                FirebaseDBMng.initFirebaseDB()
                // initialization of user followers (Heroku backend)
                FirebaseDBMng.getFollowingsUser()

                if (this.intent?.extras?.getString("frgToLoad") == null)
                    setContentView(R.layout.activity_logged)
                val navView: BottomNavigationView = findViewById(R.id.nav_view)

                val navController = findNavController(R.id.nav_host_fragment)
                NavigationUI.setupActionBarWithNavController(this, navController)

                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.
                val appBarConfiguration = AppBarConfiguration(setOf(
                        R.id.navigation_home, R.id.navigation_diary, R.id.navigation_progress, R.id.navigation_profile))
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                navView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

            }
        })

    }

    fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_sign_out -> {
                val postsRepository = PostsRepository(getDatabase(application))
                activityScope.launch {
                    try {
                        postsRepository.dropPosts()
                    } catch (networkError: IOException) {
                        Log.e(TAG, "$networkError")
                    }

                    FirebaseAuth.getInstance().signOut()
                    FirebaseDBMng.resetUserInfo()
                }
            }
            R.id.app_bar_message -> {
                val intent = Intent(this, MessagesActivity::class.java)
                // Log.e("LOGGED ACTIVITY", "${this.intent?.extras?.getString("frgToLoad")}")
                intent.putExtra("frgToLoad", this.intent?.extras?.getString("frgToLoad"))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val myActionMenuItem = menu!!.findItem(R.id.app_bar_search)
        val searchView = myActionMenuItem.actionView as SearchView
        searchView.queryHint = "Search on FoodLocker"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Toast like print
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }
                myActionMenuItem.collapseActionView()
                startActivitySearch(query)

                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun startActivitySearch(query: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra("searchElement", query)
        intent.putExtra("frgToLoad", this.intent?.extras?.getString("frgToLoad"))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}
