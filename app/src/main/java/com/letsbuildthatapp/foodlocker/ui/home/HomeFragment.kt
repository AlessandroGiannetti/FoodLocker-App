package com.letsbuildthatapp.foodlocker.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.FragmentHomeBinding
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.utility.ApiStatus


private const val TAG = "HOME FRAGMENT"

class HomeFragment : Fragment() {

    /**
     * Lazily initialize our [HomeViewModel].
     */
    private val viewModel: HomeViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, HomeViewModel.Factory(activity.application))
                .get(HomeViewModel::class.java)
    }
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        activity?.intent?.putExtra("frgToLoad", "HOME_FRAGMENT")

        binding = FragmentHomeBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.postList.adapter = PostListAdapter()
        val adapter = binding.postList.adapter
        adapter?.notifyDataSetChanged()

        FirebaseDBMng.auth.currentUser?.getIdToken(true)
                ?.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful)
                        return@OnCompleteListener

                    // Get new Instance ID token
                    val token = task.result?.token
                    Log.e("token", token.toString())
                })

        // click listener on
        binding.addPostLayout.setOnClickListener {
            val myIntent = Intent(context, WritePostActivity::class.java)
            context?.startActivity(myIntent)
        }

        // wait until user info from firebase are fetched
        FirebaseDBMng.userDBinfo.observe(viewLifecycleOwner, Observer { userInfo ->
            if (userInfo != null) {
                viewModel.loadMyProfileImage(binding.imageviewProfilePost)
            }
        })

        // Observer for the network error.
        viewModel.status.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError == ApiStatus.ERROR) onNetworkError()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        Log.e(TAG, "COMING FROM: ${activity?.intent?.extras?.getString("frgToLoadFromActivity")}")
        when (activity?.intent?.extras?.getString("frgToLoadFromActivity")) {
            "PROGRESS_FRAGMENT" -> {
                activity?.intent?.putExtra("frgToLoadFromActivity", "VOID")
                view.findNavController().navigate(R.id.navigation_progress)
            }
            "PROFILE_FRAGMENT" -> {
                activity?.intent?.putExtra("frgToLoadFromActivity", "VOID")
                view.findNavController().navigate(R.id.navigation_profile)
            }
            "DIARY_FRAGMENT" -> {
                activity?.intent?.putExtra("frgToLoadFromActivity", "VOID")
                view.findNavController().navigate(R.id.navigation_diary)
            }
            "LATEST_MESSAGE_FRAGMENT" -> {
                activity?.intent?.putExtra("frgToLoadFromActivity", "VOID")
                view.findNavController().navigate(R.id.latestMessagesFragment)
            }
            "FOOD_SEARCH" -> {
                activity?.intent?.putExtra("frgToLoadFromActivity", "VOID")
                view.findNavController().navigate(R.id.searchFoodFragment)
            }

        }

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


}