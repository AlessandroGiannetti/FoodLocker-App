package com.letsbuildthatapp.foodlocker.ui.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentRegistrationBinding

private const val TAG = "Register fragment"

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    private lateinit var ViewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_registration,
                container,
                false)

        ViewModel =
                ViewModelProvider(this).get(RegistrationViewModel::class.java)

        // set the viewModel for data binding - this allows the bound layout access
        //to all the data in the viewModel
        binding.viewModel = ViewModel

        // specify the fragment view as the lifecycle owner of the binding
        //this is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // listener on  button for select image profile
        binding.selectphotoButtonRegister.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0)
        }

        // resume value if not null
        if (ViewModel.bitmap != null) {
            binding.selectphotoImageviewRegister.setImageBitmap(ViewModel.bitmap)
            binding.selectphotoButtonRegister.alpha = 0f
        }

        return binding.root
    }

    // used to get images from android gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            ViewModel.selectedPhotoUri = data.data
            ViewModel.bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, ViewModel.selectedPhotoUri)

            binding.selectphotoImageviewRegister.setImageBitmap(ViewModel.bitmap)
            binding.selectphotoButtonRegister.alpha = 0f
        }
    }


}


