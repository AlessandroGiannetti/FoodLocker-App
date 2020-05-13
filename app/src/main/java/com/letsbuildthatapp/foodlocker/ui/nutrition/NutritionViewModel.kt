package com.letsbuildthatapp.foodlocker.ui.nutrition

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.models.DayProperty
import com.letsbuildthatapp.foodlocker.models.FoodProperty
import com.letsbuildthatapp.foodlocker.network.DayApi
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.FoodApi
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "NUTRITION VIEW MODEL"

class NutritionViewModel : ViewModel() {

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

    private val _allDayFoods = MutableLiveData<List<FoodProperty>>()
    val allDayFoods: LiveData<List<FoodProperty>>
        get() = _allDayFoods

    // key: [1]->breakfast, [2]->lunch, [3]->dinner, [4]->snack [5]->tot
    // value: [0]->calories, [1]->carb, [2]->fat, [3]->pro
    var nutritionPerMeal = HashMap<Int, MutableList<Float>>()

    private val _done = MutableLiveData<Boolean>()
    val done: LiveData<Boolean>
        get() = _done

    init {
        nutritionPerMeal = hashMapOf(1 to mutableListOf(0f, 0f, 0f, 0f), 2 to mutableListOf(0f, 0f, 0f, 0f), 3 to mutableListOf(0f, 0f, 0f, 0f), 4 to mutableListOf(0f, 0f, 0f, 0f), 5 to mutableListOf(0f, 0f, 0f, 0f))
    }

    fun getDayProperties(date: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = DayApi.retrofitServiceGetDay.getDay(FirebaseDBMng.auth.currentUser?.uid.toString(), date)
            try {
                _status.value = ApiStatus.LOADING
                val result = getPropertiesDeferred.await()
                _status.value = ApiStatus.DONE
                if (result.date == null)
                    Log.e(TAG, "NO MANAGED")
                else {
                    _day.value = result
                    getFoodProperties(date)
                }
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _day.value = null
            }
        }
    }

    fun getFoodProperties(date: String) {
        coroutineScope.launch {
            val getPropertiesDeferred = FoodApi.retrofitServiceGetDiaryFoods.getDiaryFood(FirebaseDBMng.auth.currentUser?.uid.toString(), date)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _status.value = ApiStatus.DONE
                _allDayFoods.value = listResult
                caloriesAndFoodManagement()
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _allDayFoods.value = ArrayList()
            }
        }
    }

    private fun caloriesAndFoodManagement() {

        if (allDayFoods.value != null)
            for (food in allDayFoods.value!!) {
                nutritionPerMeal[5]!![0] += food.energy_kcal!!.toFloat() // tot calories
                nutritionPerMeal[5]!![1] += food.carbohydrt_g!!.toFloat() // tot carb
                nutritionPerMeal[5]!![2] += food.lipid_tot_g!!.toFloat() // tot fat
                nutritionPerMeal[5]!![3] += food.protein_g!!.toFloat() // tot pro

                when (food.meal) {
                    1 -> {
                        nutritionPerMeal[1]!![0] += food.energy_kcal.toFloat() // calories
                        nutritionPerMeal[1]!![1] += food.carbohydrt_g.toFloat() // carb
                        nutritionPerMeal[1]!![2] += food.lipid_tot_g.toFloat() // fat
                        nutritionPerMeal[1]!![3] += food.protein_g.toFloat() // pro
                    }
                    2 -> {
                        nutritionPerMeal[2]!![0] += food.energy_kcal.toFloat() // calories
                        nutritionPerMeal[2]!![1] += food.carbohydrt_g.toFloat() // carb
                        nutritionPerMeal[2]!![2] += food.lipid_tot_g.toFloat() // fat
                        nutritionPerMeal[2]!![3] += food.protein_g.toFloat() // pro
                    }
                    3 -> {
                        nutritionPerMeal[3]!![0] += food.energy_kcal.toFloat() // calories
                        nutritionPerMeal[3]!![1] += food.carbohydrt_g.toFloat() // carb
                        nutritionPerMeal[3]!![2] += food.lipid_tot_g.toFloat() // fat
                        nutritionPerMeal[3]!![3] += food.protein_g.toFloat() // pro
                    }
                    4 -> {
                        nutritionPerMeal[4]!![0] += food.energy_kcal.toFloat() // calories
                        nutritionPerMeal[4]!![1] += food.carbohydrt_g.toFloat() // carb
                        nutritionPerMeal[4]!![2] += food.lipid_tot_g.toFloat() // fat
                        nutritionPerMeal[4]!![3] += food.protein_g.toFloat() // pro
                    }
                }
            }
        Log.e(TAG, "$nutritionPerMeal")
        _done.value = true
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
