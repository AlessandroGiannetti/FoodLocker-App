package com.letsbuildthatapp.foodlocker.ui.diary.addSport

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.models.SportProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.SportApi
import com.letsbuildthatapp.foodlocker.ui.activity.LoggedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "SEARCH FOOD VIEW MODEL"

enum class SearchDetailFoodApiStatus { LOADING, ERROR, DONE }

class AddSportViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    var hour = ""

    private val _status = MutableLiveData<SearchDetailFoodApiStatus>()
    val status: LiveData<SearchDetailFoodApiStatus>
        get() = _status

    private val _sport = MutableLiveData<SportProperty>()
    val sport: LiveData<SportProperty>
        get() = _sport

    private val _calories = MutableLiveData<String>()
    val calories: LiveData<String>
        get() = _calories

    private val _hourError = MutableLiveData<String>()
    val hourError: LiveData<String>
        get() = _hourError

    init {
        _hourError.value = ""
    }

    fun getSportProperty(sportId: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = SportApi.retrofitServiceGetSportsById.getSportById(sportId)
            try {
                _status.value = SearchDetailFoodApiStatus.LOADING
                val result = getPropertiesDeferred.await()
                _status.value = SearchDetailFoodApiStatus.DONE
                _sport.value = result

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = SearchDetailFoodApiStatus.ERROR
                _sport.value = null
            }
        }
    }

    fun setSportOnDiary(sportId: String, activity: AddSportActivity) {
        coroutineScope.launch {
            val setPropertiesDeferred = SportApi
                    .retrofitServiceSetSportOnDiary
                    .setSportOnDiary(FirebaseDBMng.auth.currentUser?.uid.toString(), LocalDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ISO_DATE), sportId, hour)
            try {
                _status.value = SearchDetailFoodApiStatus.LOADING
                setPropertiesDeferred.await()
                _status.value = SearchDetailFoodApiStatus.DONE
                Toast.makeText(activity, "${sport.value?.name} Added", Toast.LENGTH_LONG).show()
                val myIntent = Intent(activity, LoggedActivity::class.java)
                myIntent.putExtra("frgToLoadFromActivity", "DIARY_FRAGMENT")
                activity.startActivity(myIntent)

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = SearchDetailFoodApiStatus.ERROR
                _sport.value = null
            }
        }
    }

    fun setSport(activity: AddSportActivity, sportId: String) {
        _hourError.value = null

        if (hour.isEmpty()) {
            _hourError.value = "Please insert the minutes of your exercise"
            return
        } else
            setSportOnDiary(sportId, activity)
    }

    fun setCalories(calories: Int) {
        this._calories.value = calories.toString()
    }

}
