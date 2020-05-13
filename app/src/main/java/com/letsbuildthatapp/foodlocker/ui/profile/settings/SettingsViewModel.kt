package com.letsbuildthatapp.foodlocker.ui.profile.settings

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.models.WeightProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

private const val TAG = "SETTINGS VIEW MODEL"


class SettingsViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown


    private val _properties = MutableLiveData<List<WeightProperty>>()
    val properties: LiveData<List<WeightProperty>>
        get() = _properties

    private val _username = MutableLiveData<String>()
    val username: LiveData<String>
        get() = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email


    val genders = arrayOf("Male", "Female")

    val _gender = MutableLiveData<String>()
    val gender: LiveData<String>
        get() = _gender
    val _heightM = MutableLiveData<String>()
    val heightM: LiveData<String>
        get() = _heightM
    val _weightKg = MutableLiveData<String>()
    val weightKg: LiveData<String>
        get() = _weightKg
    val _targetWeightKg = MutableLiveData<String>()
    val targetWeightKg: LiveData<String>
        get() = _targetWeightKg
    val _water = MutableLiveData<String>()
    val water: LiveData<String>
        get() = _water
    val _weaklyHourSport = MutableLiveData<String>()
    val weaklyHourSport: LiveData<String>
        get() = _weaklyHourSport
    val _dailyKcal = MutableLiveData<String>()
    val dailyKcal: LiveData<String>
        get() = _dailyKcal
    val _dateOfBirthday = MutableLiveData<String>()
    val dateOfBirthday: LiveData<String>
        get() = _dateOfBirthday

    var genderV: String = ""
    var heightMV: String = ""
    var weightKgV: String = ""
    var targetWeightKgV: String = ""
    var waterV: String = ""
    var weaklyHourSportV: String = ""
    var dailyKcalV: String = ""
    var dateOfBirthdayV: MutableLiveData<String> = MutableLiveData()


    val _dateOfBirthdayError = MutableLiveData<String>()
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
        if (FirebaseDBMng.userDBinfo.value != null) {
            _status.value = ApiStatus.LOADING
            _dateOfBirthday.value = FirebaseDBMng.userDBinfo.value?.dateOfBirthday
            _username.value = FirebaseDBMng.userDBinfo.value?.username
            _email.value = FirebaseDBMng.auth.currentUser?.email
            _dailyKcal.value = FirebaseDBMng.userDBinfo.value?.daily_kcal.toString()
            _weaklyHourSport.value = FirebaseDBMng.userDBinfo.value?.weakly_hour_sport.toString()
            _water.value = FirebaseDBMng.userDBinfo.value?.water.toString()
            _targetWeightKg.value = FirebaseDBMng.userDBinfo.value?.target_weight_kg.toString()
            _weightKg.value = FirebaseDBMng.userDBinfo.value?.weight_kg.toString()
            _heightM.value = FirebaseDBMng.userDBinfo.value?.height_m.toString()
            _gender.value = FirebaseDBMng.userDBinfo.value?.gender
            _status.value = ApiStatus.DONE
        } else
            _status.value = ApiStatus.ERROR

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

        if (status.value == ApiStatus.ERROR)
            return

        FirebaseDBMng.setExtraInfo(
                dateOfBirthdayV.value.toString(),
                genderV,
                heightMV.toInt(),
                weightKgV.toInt(),
                targetWeightKgV.toInt(),
                waterV.toInt(),
                weaklyHourSportV.toInt(),
                dailyKcalV.toInt())

        FirebaseDBMng.initFirebaseDB()
        Toast.makeText(view.context, "the data have been updated", Toast.LENGTH_LONG).show()

    }

    private fun isFormDataValid(): Boolean {
        if (heightMV == "")
            heightMV = heightM.value!!
        if (weightKgV == "")
            weightKgV = weightKg.value!!
        if (targetWeightKgV == "")
            targetWeightKgV = targetWeightKg.value!!
        if (waterV == "")
            waterV = water.value!!
        if (weaklyHourSportV == "")
            weaklyHourSportV = weaklyHourSport.value!!
        if (dailyKcalV == "")
            dailyKcalV = dailyKcal.value!!
        if (genderV == "")
            genderV = gender.value!!
        if (dateOfBirthdayV.value == "")
            dateOfBirthdayV.value = dateOfBirthday.value

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

        when (heightMV.toIntOrNull()) {
            null -> _heightMError.value = "insert height"
            !in 50..280 -> _heightMError.value = "Insert a value [50-280]"
            else -> validForm++
        }

        when (weightKgV.toIntOrNull()) {
            null -> _weightKgError.value = "Insert your weight"
            !in 30..220 -> _weightKgError.value = "Insert a value: [30-220]"
            else -> validForm++
        }

        when (targetWeightKgV.toIntOrNull()) {
            null -> _targetWeightKgError.value = "Insert target weight"
            !in 20..220 -> _targetWeightKgError.value = "Insert a value [30-220]"
            else -> validForm++
        }

        when (waterV.toIntOrNull()) {
            null -> _waterError.value = "insert water assumed daily"
            !in 1..7 -> _waterError.value = "Insert a value: [1-7]"
            else -> validForm++
        }

        when (weaklyHourSport.value?.toIntOrNull()) {
            null -> _weaklyHourSportError.value = "Insert weakly sport"
            !in 1..42 -> _weaklyHourSportError.value = "Insert a value: [0-42]"
            else -> validForm++
        }

        when (dailyKcalV.toIntOrNull()) {
            null -> _dailyKcalError.value = "Insert your daily kcal"
            !in 200..5000 -> _dailyKcalError.value = "Insert a value [200-5000]"
            else -> validForm++
        }

        when (genderV.isEmpty()) {
            true -> _genderError.value = "Insert your gender"
            false -> validForm++
        }

        when (dateOfBirthdayV.value == null) {
            true -> _dateOfBirthdayError.value = "Insert a valid date"
            false -> validForm++
        }

        return validForm == 8
    }

    fun setDate(date: String) {
        dateOfBirthdayV.value = date
    }

    fun loadMyProfileImage(imageviewProfile: CircleImageView) {
        if (FirebaseDBMng.userDBinfo.value?.profileImageUrl != null) {
            Picasso.get().load(FirebaseDBMng.userDBinfo.value!!.profileImageUrl)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image).into(imageviewProfile)
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


