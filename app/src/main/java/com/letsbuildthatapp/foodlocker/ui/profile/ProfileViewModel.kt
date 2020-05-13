package com.letsbuildthatapp.foodlocker.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.models.UserProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.UserApi
import com.letsbuildthatapp.foodlocker.network.WeightApi
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import com.letsbuildthatapp.foodlocker.utility.Utility
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val UNITWEIGHT = "Kg"
private const val UNITLENGHT = "Cm"
private const val UNITCALORIES = "Kcal"

private const val TAG = "PROFILE VIEW MODEL"

class ProfileViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _followers = MutableLiveData<List<UserProperty>>()
    val followers: LiveData<List<UserProperty>>
        get() = _followers

    private val _following = MutableLiveData<List<UserProperty>>()
    val following: LiveData<List<UserProperty>>
        get() = _following

    private val _Nfollowers = MutableLiveData<String>()
    val Nfollowers: LiveData<String>
        get() = _Nfollowers

    private val _Nfollowing = MutableLiveData<String>()
    val Nfollowing: LiveData<String>
        get() = _Nfollowing

    private val _bmi = MutableLiveData<String>()
    val bmi: LiveData<String>
        get() = _bmi

    private val _bmiStatus = MutableLiveData<String>()
    val bmiStatus: LiveData<String>
        get() = _bmiStatus


    private val _lastWeight = MutableLiveData<String>()
    val lastWeight: LiveData<String>
        get() = _lastWeight

    private val _height = MutableLiveData<String>()
    val height: LiveData<String>
        get() = _height

    private val _target = MutableLiveData<String>()
    val target: LiveData<String>
        get() = _target

    private val _basalKcal = MutableLiveData<String>()
    val basalKcal: LiveData<String>
        get() = _basalKcal

    private val _KgLoss = MutableLiveData<String>()
    val KgLoss: LiveData<String>
        get() = _KgLoss

    private val _goal = MutableLiveData<String>()
    val goal: LiveData<String>
        get() = _goal


    init {
        getLastWeight()
        _username.value = FirebaseDBMng.userDBinfo.value?.username
        _email.value = FirebaseDBMng.auth.currentUser?.email
        _height.value = if (FirebaseDBMng.userDBinfo.value?.height_m != null) FirebaseDBMng.userDBinfo.value?.height_m.toString().plus(UNITLENGHT) else ""
        getFollowers()
        getFollowed()
    }


    fun getFollowers() {
        coroutineScope.launch {
            val getPropertiesDeferred = UserApi.retrofitServiceGetFollowers.getFollowers(FirebaseDBMng.auth.currentUser?.uid.toString())
            try {
                _status.value = ApiStatus.LOADING
                val result = getPropertiesDeferred.await()
                _Nfollowers.value = result.size.toString()
                _followers.value = result

                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
            }
        }
    }

    fun getFollowed() {
        coroutineScope.launch {
            val getPropertiesDeferred = UserApi.retrofitServiceGetFollowed.getFollowed(FirebaseDBMng.auth.currentUser?.uid.toString())
            try {
                _status.value = ApiStatus.LOADING
                val result = getPropertiesDeferred.await()
                _Nfollowing.value = result.size.toString()
                _following.value = result
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
            }
        }
    }

    private fun getLastWeight() {
        coroutineScope.launch {
            val getPropertiesDeferred = WeightApi.retrofitServiceAllWeights.getWeights(FirebaseDBMng.auth.currentUser?.uid.toString())
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                val actualWeight = listResult.first().weight
                _lastWeight.value = actualWeight.plus(UNITWEIGHT)
                Utility.setWeight(actualWeight)

                // now that we have the weight we can calculate the basal metabolism
                _basalKcal.value = Utility.basalMetabolism().plus(UNITCALORIES)
                _target.value = Utility.targetWeight.toString().plus(UNITWEIGHT)
                val bmi = Utility.computeBMI()
                _bmi.value = bmi.first
                _bmiStatus.value = bmi.second
                _KgLoss.value = Utility.progress().take(4).plus(UNITWEIGHT)
                _goal.value = "(" + Utility.goal.take(4).plus(UNITWEIGHT) + ")"

                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
            }
        }
    }

    fun loadProfileImage(imageviewProfile: CircleImageView) {
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

}