package com.letsbuildthatapp.foodlocker.ui.registration

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.models.WeightProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.WeightApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "Extra info Registration View Model"

enum class WeightRegistrationApiStatus { LOADING, ERROR, DONE }

class ExtraInfoRegistrationViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<WeightRegistrationApiStatus>()
    val status: LiveData<WeightRegistrationApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<WeightProperty>>()
    val properties: LiveData<List<WeightProperty>>
        get() = _properties


    val genders = arrayOf("Male", "Female")

    var gender: String = ""
    var heightM: String = ""
    var weightKg: String = ""
    var targetWeightKg: String = ""
    var water: String = ""
    var weaklyHourSport: String = ""
    var dailyKcal: String = ""
    var dateOfBirthday: MutableLiveData<String> = MutableLiveData()


    private var _dateOfBirthdayError = MutableLiveData<String>()
    val dateOfBirthdayError: LiveData<String>
        get() = _dateOfBirthdayError

    private val _heightMError = MutableLiveData<String>()
    val heightMError: LiveData<String>
        get() = _heightMError

    private val _weightKgError = MutableLiveData<String>()
    val weightKgError: LiveData<String>
        get() = _weightKgError

    private val _targetWeightKgError = MutableLiveData<String>()
    val targetWeightKgError: LiveData<String>
        get() = _targetWeightKgError

    private val _waterError = MutableLiveData<String>()
    val waterError: LiveData<String>
        get() = _waterError

    private val _weaklyHourSportError = MutableLiveData<String>()
    val weaklyHourSportError: LiveData<String>
        get() = _weaklyHourSportError

    private val _dailyKcalError = MutableLiveData<String>()
    val dailyKcalError: LiveData<String>
        get() = _dailyKcalError

    private val _genderError = MutableLiveData<String>()
    val genderError: LiveData<String>
        get() = _genderError

    init {
        _dateOfBirthdayError.value = ""
        _heightMError.value = ""
        _weightKgError.value = ""
        _targetWeightKgError.value = ""
        _waterError.value = ""
        _weaklyHourSportError.value = ""
        _dailyKcalError.value = ""
        _genderError.value = ""
    }

    fun regLogin(view: View) {

        if (!isFormDataValid())
            return

        setUserWeight(FirebaseDBMng.auth.currentUser?.uid!!, weightKg, null)

        if (status.value == WeightRegistrationApiStatus.ERROR)
            return

        FirebaseDBMng.setExtraInfo(
                dateOfBirthday.value!!.toString(),
                gender,
                heightM.toInt(),
                weightKg.toInt(),
                targetWeightKg.toInt(),
                water.toInt(),
                weaklyHourSport.toInt(),
                dailyKcal.toInt())

        snackBarMessage(view, "Extra information successfully registered")
        view.findNavController().navigate(R.id.action_extraInfoRegistration_to_loggedActivity)
    }

    private fun setUserWeight(uuid: String, weight: String, photo: String?) {
        coroutineScope.launch {
            val setUserWeightDeferred = WeightApi.retrofitServiceSetWeight.setWeight(uuid, weight, photo)
            try {
                _status.value = WeightRegistrationApiStatus.LOADING
                setUserWeightDeferred.await()
                _status.value = WeightRegistrationApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = WeightRegistrationApiStatus.ERROR
                _properties.value = ArrayList()
                Log.e(TAG, "$e - $properties")
            }
        }
    }

    private fun isFormDataValid(): Boolean {

        Log.e("reg", "$heightM $weightKg $targetWeightKg $water $weaklyHourSport $dailyKcal")
        var validForm = 0

        // init of the message error
        _dateOfBirthdayError.value = null
        _heightMError.value = null
        _weightKgError.value = null
        _targetWeightKgError.value = null
        _waterError.value = null
        _weaklyHourSportError.value = null
        _dailyKcalError.value = null
        _genderError.value = null

        when (heightM.toIntOrNull()) {
            null -> _heightMError.value = "insert height"
            !in 50..280 -> _heightMError.value = "Insert a value [50-280]"
            else -> validForm++
        }

        when (weightKg.toIntOrNull()) {
            null -> _weightKgError.value = "Insert your weight"
            !in 30..220 -> _weightKgError.value = "Insert a value: [30-220]"
            else -> validForm++
        }

        when (targetWeightKg.toIntOrNull()) {
            null -> _targetWeightKgError.value = "Insert target weight"
            !in 20..220 -> _targetWeightKgError.value = "Insert a value [30-220]"
            else -> validForm++
        }

        when (water.toIntOrNull()) {
            null -> _waterError.value = "insert water assumed daily"
            !in 1..7 -> _waterError.value = "Insert a value: [1-7]"
            else -> validForm++
        }

        when (weaklyHourSport.toIntOrNull()) {
            null -> _weaklyHourSportError.value = "Insert weakly sport"
            !in 1..42 -> _weaklyHourSportError.value = "Insert a value: [0-42]"
            else -> validForm++
        }

        when (dailyKcal.toIntOrNull()) {
            null -> _dailyKcalError.value = "Insert your daily kcal"
            !in 200..5000 -> _dailyKcalError.value = "Insert a value [200-5000]"
            else -> validForm++
        }

        when (gender.isEmpty()) {
            true -> _genderError.value = "Insert your gender"
            false -> validForm++
        }

        when (dateOfBirthday.value == null) {
            true -> _dateOfBirthdayError.value = "Insert a valid date"
            false -> validForm++
        }

        return validForm == 8
    }

    private fun snackBarMessage(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .show()
    }

    fun setDate(date: String) {
        dateOfBirthday.value = date
    }


}


