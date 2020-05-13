package com.letsbuildthatapp.foodlocker.ui.home

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.*
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.database.getDatabase
import com.letsbuildthatapp.foodlocker.models.PostProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.repository.PostsRepository
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "HOME VIEW MODEL"

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from.
     */
    private val postsRepository = PostsRepository(getDatabase(application))

    /**
     * all Posts displayed on the Home page.
     */
    val homePosts = postsRepository.posts

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = SupervisorJob()

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val viewModelScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    var content: String = ""

    //image post insertion
    var selectedPhotoUri: Uri? = null
    var bitmap: Bitmap? = null

    private val _properties = MutableLiveData<MutableList<PostProperty>>()
    val properties: LiveData<MutableList<PostProperty>>
        get() = _properties

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    init {
        refreshDataFromRepository()
        _username.value = FirebaseDBMng.userDBinfo.value?.username
    }

    /**
     * Refresh data from the repository. Use a coroutine launch to run in a
     * background thread.
     */
    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                postsRepository.refreshPosts()
                _status.value = ApiStatus.DONE

            } catch (networkError: IOException) {
                // Show a Toast error message and hide the progress bar.
                _status.value = ApiStatus.ERROR
            }
        }
    }

    fun setPost(activity: WritePostActivity): Boolean {
        return if (content == "") {
            Toast.makeText(activity, "empty post", Toast.LENGTH_SHORT).show()
            false
        } else
            FirebaseDBMng.savePostOnBackend(selectedPhotoUri, activity, content)
    }

    fun loadImage(imageviewProfile: CircleImageView) {
        Picasso.get().load(FirebaseDBMng.userDBinfo.value?.profileImageUrl).into(imageviewProfile)
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * load personal image profile .
     */
    fun loadMyProfileImage(imageviewProfile: CircleImageView) {
        Picasso.get().load(FirebaseDBMng.userDBinfo.value!!.profileImageUrl)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image).into(imageviewProfile)
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Factory for constructing HomeViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}