package com.letsbuildthatapp.foodlocker.ui.searchUsers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.models.UserProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.UserApi
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "Search View Model"

enum class SearchApiStatus { LOADING, ERROR, DONE, NO_CONTENT }

class SearchResultsViewModel : ViewModel() {

    var queryParameter: String = ""

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    private val _properties = MutableLiveData<List<UserProperty>>()
    val properties: LiveData<List<UserProperty>>
        get() = _properties

    init {
        getUsersProperties()
    }


    private fun getUsersProperties() {
        coroutineScope.launch {
            val getPropertiesDeferred = UserApi.retrofitServiceGetUsers.getUsers(FirebaseDBMng.userDBinfo.value?.uid.toString(), queryParameter)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _status.value = ApiStatus.DONE
                if (listResult.isEmpty())
                    _status.value = ApiStatus.NO_CONTENT
                _properties.value = listResult
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

}