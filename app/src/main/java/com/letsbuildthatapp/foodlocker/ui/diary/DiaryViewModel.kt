package com.letsbuildthatapp.foodlocker.ui.diary

import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.models.DayProperty
import com.letsbuildthatapp.foodlocker.models.FoodProperty
import com.letsbuildthatapp.foodlocker.models.SportProperty
import com.letsbuildthatapp.foodlocker.network.DayApi
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.FoodApi
import com.letsbuildthatapp.foodlocker.network.SportApi
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "DIARY VIEW MODEL"

class DiaryViewModel : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    private var _isNetworkErrorShown = MutableLiveData<Boolean>(false)
    val isNetworkErrorShown: LiveData<Boolean>
        get() = _isNetworkErrorShown

    private val _day = MutableLiveData<DayProperty>()
    val day: LiveData<DayProperty>
        get() = _day

    var foodsByMeal = HashMap<Int, MutableList<FoodProperty>>()

    private val _allDayFoods = MutableLiveData<List<FoodProperty>>()
    val allDayFoods: LiveData<List<FoodProperty>>
        get() = _allDayFoods

    //sublist of meal foods
    private val _foodsBreakfast = MutableLiveData<List<FoodProperty>>()
    val foodsBreakfast: LiveData<List<FoodProperty>>
        get() = _foodsBreakfast
    private val _foodsLunch = MutableLiveData<List<FoodProperty>>()
    val foodsLunch: LiveData<List<FoodProperty>>
        get() = _foodsLunch
    private val _foodsDinner = MutableLiveData<List<FoodProperty>>()
    val foodsDinner: LiveData<List<FoodProperty>>
        get() = _foodsDinner
    private val _foodsSnack = MutableLiveData<List<FoodProperty>>()
    val foodsSnack: LiveData<List<FoodProperty>>
        get() = _foodsSnack

    // list of exercises/sports
    private val _sports = MutableLiveData<List<SportProperty>>()
    val sports: LiveData<List<SportProperty>>
        get() = _sports

    // calories top bar values
    private val _targetKcal = MutableLiveData<String>()
    val targetKcal: LiveData<String>
        get() = _targetKcal
    private val _foodKcal = MutableLiveData<String>()
    val foodKcal: LiveData<String>
        get() = _foodKcal
    private val _exerciseKcal = MutableLiveData<String>()
    val exerciseKcal: LiveData<String>
        get() = _exerciseKcal
    private val _remainingKcal = MutableLiveData<String>()
    val remainingKcal: LiveData<String>
        get() = _remainingKcal

    // partial calories per meal values
    private val _breakfastKcal = MutableLiveData<String>()
    val breakfastKcal: LiveData<String>
        get() = _breakfastKcal
    private val _lunchKcal = MutableLiveData<String>()
    val lunchKcal: LiveData<String>
        get() = _lunchKcal
    private val _dinnerKcal = MutableLiveData<String>()
    val dinnerKcal: LiveData<String>
        get() = _dinnerKcal
    private val _snackKcal = MutableLiveData<String>()
    val snackKcal: LiveData<String>
        get() = _snackKcal

    init {
        foodsByMeal = hashMapOf(1 to mutableListOf(), 2 to mutableListOf(), 3 to mutableListOf(), 4 to mutableListOf(), -1 to mutableListOf())
        if (FirebaseDBMng.userDBinfo.value?.daily_kcal != null)
            _targetKcal.value = FirebaseDBMng.userDBinfo.value?.daily_kcal.toString()
        else
            _targetKcal.value = ""
        getDayProperties(LocalDateTime.now(ZoneId.of("Europe/Rome")).format(DateTimeFormatter.ISO_DATE))
    }


    private fun getFoodProperties(date: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = FoodApi.retrofitServiceGetDiaryFoods.getDiaryFood(FirebaseDBMng.auth.currentUser?.uid.toString(), date)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _allDayFoods.value = listResult
                getSportProperties(date)
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _allDayFoods.value = ArrayList()
            }
        }
    }

    private fun getSportProperties(date: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = SportApi.retrofitServiceGetDiarySports.getDiarySport(FirebaseDBMng.auth.currentUser?.uid.toString(), date)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _sports.value = listResult
                caloriesAndFoodManagement()
                _status.value = ApiStatus.DONE

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _sports.value = ArrayList()
            }
        }
    }

    fun getDayProperties(date: String) {
        Log.e(TAG, date)
        coroutineScope.launch {
            val getPropertiesDeferred = DayApi.retrofitServiceGetDay.getDay(FirebaseDBMng.auth.currentUser?.uid.toString(), date)
            try {
                _status.value = ApiStatus.LOADING
                val result = getPropertiesDeferred.await()
                if (result.date == null)
                    setDayProperties(date)
                else {
                    _day.value = result
                    getFoodProperties(date)
                }
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _day.value = null
            }
        }
    }

    private fun setDayProperties(date: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = DayApi.retrofitServiceSetDay.setDay(FirebaseDBMng.auth.currentUser?.uid.toString(), date, 0, "")
            try {
                val result = getPropertiesDeferred.await()
                _day.value = result
                getFoodProperties(date)
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _day.value = null
            }

        }
    }

    private fun caloriesAndFoodManagement() {
        var totalCaloriesFood = 0
        var totalCaloriesExercise = 0
        var totalCaloriesBreakfast = 0
        var totalCaloriesLunch = 0
        var totalCaloriesDinner = 0
        var totalCaloriesSnack = 0

        if (allDayFoods.value != null)
            for (food in allDayFoods.value!!) {
                totalCaloriesFood += food.energy_kcal?.toInt()!!

                when (food.meal) {
                    1 -> {
                        totalCaloriesBreakfast += food.energy_kcal.toInt()
                        foodsByMeal[1]?.add(food)
                    }
                    2 -> {
                        totalCaloriesLunch += food.energy_kcal.toInt()
                        foodsByMeal[2]?.add(food)
                    }
                    3 -> {
                        totalCaloriesDinner += food.energy_kcal.toInt()
                        foodsByMeal[3]?.add(food)
                    }
                    4 -> {
                        totalCaloriesSnack += food.energy_kcal.toInt()
                        foodsByMeal[4]?.add(food)
                    }
                    else -> foodsByMeal[-1]?.add(food)
                }
            }

        if (sports.value != null)
            for (sport in sports.value!!) {
                totalCaloriesExercise += (sport.calories?.toInt()?.times(sport.hour!!)!!).div(60)
            }
        // sublist of foods divided by meal
        _foodsBreakfast.value = foodsByMeal[1]
        _foodsLunch.value = foodsByMeal[2]
        _foodsDinner.value = foodsByMeal[3]
        _foodsSnack.value = foodsByMeal[4]
        // top bar calories values
        _foodKcal.value = totalCaloriesFood.toString()
        _exerciseKcal.value = totalCaloriesExercise.toString()
        _remainingKcal.value = (targetKcal.value?.toInt()?.minus(totalCaloriesFood)?.plus(totalCaloriesExercise)).toString()
        // total meal calories
        _breakfastKcal.value = totalCaloriesBreakfast.toString()
        _lunchKcal.value = totalCaloriesLunch.toString()
        _dinnerKcal.value = totalCaloriesDinner.toString()
        _snackKcal.value = totalCaloriesSnack.toString()
    }

    fun addFood(view: View, mealType: String) {
        view.findNavController().navigate(R.id.action_navigation_diary_to_searchFoodFragment, bundleOf("mealType" to mealType))
    }

    fun addExercise(view: View) {
        view.findNavController().navigate(R.id.action_navigation_diary_to_searchSportFragment)
    }


    /**
     * Resets the network error flag.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

}
