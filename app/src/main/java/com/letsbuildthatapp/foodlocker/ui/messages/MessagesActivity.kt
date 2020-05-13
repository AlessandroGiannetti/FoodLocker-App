package com.letsbuildthatapp.foodlocker.ui.messages

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity


class MessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (this.intent?.extras?.getString("ROOT") == "LATEST_MESSAGE_FRAGMENT") {
            val myIntent = Intent(this, LoggedActivity::class.java)
            myIntent.putExtra("frgToLoadFromActivity", this.intent?.extras?.getString("frgToLoad"))
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(myIntent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                if (this.intent?.extras?.getString("ROOT") == "LATEST_MESSAGE_FRAGMENT") {
                    val myIntent = Intent(this, LoggedActivity::class.java)
                    myIntent.putExtra("frgToLoadFromActivity", this.intent?.extras?.getString("frgToLoad"))
                    this.startActivity(myIntent)
                }
            }
/*                if (this.intent?.extras?.getString("ROOT") == "NEW_MESSAGE_FRAGMENT" ||
                        this.intent?.extras?.getString("ROOT") == "CHAT_LOG_FRAGMENT")
                    supportFragmentManager.popBackStack()
            }*/
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_messages)
        return navController.navigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_messages, menu)

        return super.onCreateOptionsMenu(menu)
    }


    fun setTitleToolbar(title: String) {
        this.title = title
    }


}
