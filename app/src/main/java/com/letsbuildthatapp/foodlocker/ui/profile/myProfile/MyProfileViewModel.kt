package com.letsbuildthatapp.foodlocker.ui.profile.myProfile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.models.PostProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.PostApi
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "MY PROFILE VIEW MODEL"


class MyProfileViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    private val _properties = MutableLiveData<MutableList<PostProperty>>()
    val properties: LiveData<MutableList<PostProperty>>
        get() = _properties

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email



    init {
        getPersonalPostProperties()
        _username.value = FirebaseDBMng.userDBinfo.value?.username
        _email.value = FirebaseDBMng.auth.currentUser?.email
    }


    private fun getPersonalPostProperties() {
        coroutineScope.launch {
            val getPropertiesDeferred = PostApi.retrofitServicePersonalPost.getPersonalPosts(FirebaseDBMng.auth.currentUser?.uid.toString())
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await().toMutableList()
                _status.value = ApiStatus.DONE
                if (listResult.size == 1)
                    _status.value = ApiStatus.NO_CONTENT
                _properties.value = listResult
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    fun loadMyProfileImage(imageviewProfile: CircleImageView) {
        if (FirebaseDBMng.userDBinfo.value?.profileImageUrl != null) {
            Picasso.get().load(FirebaseDBMng.userDBinfo.value!!.profileImageUrl)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image).into(imageviewProfile)
        }
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }



}
