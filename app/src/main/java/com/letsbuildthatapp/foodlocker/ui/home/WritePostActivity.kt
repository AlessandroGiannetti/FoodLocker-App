package com.letsbuildthatapp.foodlocker.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.ActivityWritePostBinding
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity


private const val TAG = "WritePost fragment"

class WritePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWritePostBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_write_post)

        viewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // listener on  button for select image profile
        binding.selectphotoButtonPost.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2)
        }

        // resume value if not null
        if (viewModel.bitmap != null)
            binding.selectphotoImageviewPost.setImageBitmap(viewModel.bitmap)


    }

    override fun onStart() {
        super.onStart()
        viewModel.loadImage(binding.imageviewProfilePost)
    }


    // used to get images from android gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            viewModel.selectedPhotoUri = data.data
            viewModel.bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, viewModel.selectedPhotoUri)

            binding.selectphotoImageviewPost.setImageBitmap(viewModel.bitmap)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_add_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.publish_post -> {
                item.isEnabled = false
                if (!viewModel.setPost(this))
                    item.isEnabled = true
            }
            android.R.id.home -> {
                val myIntent = Intent(this, LoggedActivity::class.java)
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


}
