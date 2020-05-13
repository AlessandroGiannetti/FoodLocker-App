package com.letsbuildthatapp.foodlocker.ui.progress

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.models.WeightProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.WeightApi
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val TAG = "PROGRESS VIEWMODEL"

private const val UNITWEIGHT = "Kg"

class ProgressViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    //image post insertion
    var selectedPhotoUri: Uri? = null
    var bitmap: Bitmap? = null

    // insertion
    var weight: String = ""

    private var currentWeightVal: String? = ""
    private var initWeightVal: Int? = FirebaseDBMng.userDBinfo.value?.weight_kg


    private val _property = MutableLiveData<WeightProperty>()
    val property: LiveData<WeightProperty>
        get() = _property

    private val _properties = MutableLiveData<List<WeightProperty>>()
    val properties: LiveData<List<WeightProperty>>
        get() = _properties

    private val _initWeight = MutableLiveData<String>()
    val initWeight: LiveData<String>
        get() = _initWeight

    private val _currentWeight = MutableLiveData<String>()
    val currentWeight: LiveData<String>
        get() = _currentWeight

    private val _differenceWeight = MutableLiveData<String>()
    val differenceWeight: LiveData<String>
        get() = _differenceWeight

    private val _targetWeight = MutableLiveData<String>()
    val targetWeight: LiveData<String>
        get() = _targetWeight

    private val _differenceWeightPercentage = MutableLiveData<String>()
    val differenceWeightPercentage: LiveData<String>
        get() = _differenceWeightPercentage


    init {
        _initWeight.value = if (initWeightVal != null) initWeightVal.toString().plus(UNITWEIGHT) else " "
        _targetWeight.value = if (FirebaseDBMng.userDBinfo.value?.target_weight_kg != null) FirebaseDBMng.userDBinfo.value?.target_weight_kg.toString().plus(UNITWEIGHT) else " "
        getWeightProperties()
    }


    /**
     * Sets the value of the status LiveData to the FoodLocker API status.
     */
    private fun getWeightProperties() {
        coroutineScope.launch {
            val getPropertiesDeferred = WeightApi.retrofitServiceAllWeights.getWeights(FirebaseDBMng.auth.currentUser?.uid.toString())
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()

                _properties.value = listResult
                currentWeightVal = properties.value?.first()?.weight
                _currentWeight.value = currentWeightVal.plus(UNITWEIGHT)
                computeValuesWeight()
                _status.value = ApiStatus.DONE

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

    fun setUserWeight(activity: WriteWeightActivity): Boolean {
        return if (weight == "") {
            Toast.makeText(activity, "insert your weight", Toast.LENGTH_SHORT).show()
            false
        } else
            FirebaseDBMng.saveWeightOnBackend(selectedPhotoUri, activity, weight)
    }

    private fun computeValuesWeight() {
        val diffWeight = currentWeightVal?.toFloat()?.minus(initWeightVal?.toFloat()!!)
        val percentageWeight = (diffWeight?.div(currentWeightVal?.toFloat()!!))?.times(100)?.roundToInt()?.absoluteValue!!
        _differenceWeight.value = diffWeight.toString().plus(UNITWEIGHT)

        _differenceWeightPercentage.value = if (diffWeight > 0) "(+" + percentageWeight.toString().plus("%)") else "(-" + percentageWeight.toString().plus("%)")
    }

    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

}