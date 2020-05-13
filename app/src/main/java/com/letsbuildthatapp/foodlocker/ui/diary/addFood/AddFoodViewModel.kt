package com.letsbuildthatapp.foodlocker.ui.diary.addFood

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.models.FoodProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.FoodApi
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

class AddFoodViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<SearchDetailFoodApiStatus>()
    val status: LiveData<SearchDetailFoodApiStatus>
        get() = _status

    private val _food = MutableLiveData<FoodProperty>()
    val food: LiveData<FoodProperty>
        get() = _food

    private val _carbPerc = MutableLiveData<String>()
    val carbPerc: LiveData<String>
        get() = _carbPerc
    private val _fatPerc = MutableLiveData<String>()
    val fatPerc: LiveData<String>
        get() = _fatPerc
    private val _proPerc = MutableLiveData<String>()
    val proPerc: LiveData<String>
        get() = _proPerc


    fun getFoodProperty(foodId: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = FoodApi.retrofitServiceGetFoodsById.getFoodById(foodId)
            try {
                _status.value = SearchDetailFoodApiStatus.LOADING
                val result = getPropertiesDeferred.await()
                _status.value = SearchDetailFoodApiStatus.DONE
                _food.value = result

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = SearchDetailFoodApiStatus.ERROR
                _food.value = null
            }
        }
    }

    fun setFoodOnDiary(foodId: String, meal: String, activity: AddFoodActivity) {
        coroutineScope.launch {
            val setPropertiesDeferred = FoodApi
                    .retrofitServiceSetFoodOnDiary
                    .setFoodOnDiary(FirebaseDBMng.auth.currentUser?.uid.toString(), LocalDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ISO_DATE), foodId, meal)
            try {
                _status.value = SearchDetailFoodApiStatus.LOADING
                setPropertiesDeferred.await()
                _status.value = SearchDetailFoodApiStatus.DONE
                Toast.makeText(activity, "${food.value?.name} Added", Toast.LENGTH_LONG).show()
                val myIntent = Intent(activity, LoggedActivity::class.java)
                myIntent.putExtra("frgToLoadFromActivity", "DIARY_FRAGMENT")
                activity.startActivity(myIntent)

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = SearchDetailFoodApiStatus.ERROR
                _food.value = null
            }
        }
    }

    fun setFood(activity: AddFoodActivity, foodId: String, meal: String) {
        setFoodOnDiary(foodId, meal, activity)
    }

    fun setPercFat(fat: String) {
        _fatPerc.value = fat
    }

    fun setPercCarb(carb: String) {
        _carbPerc.value = carb
    }

    fun setPercPro(pro: String) {
        _proPerc.value = pro
    }


}
