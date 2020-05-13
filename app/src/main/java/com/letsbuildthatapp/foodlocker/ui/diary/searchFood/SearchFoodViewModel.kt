package com.letsbuildthatapp.foodlocker.ui.diary.searchFood

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letsbuildthatapp.foodlocker.models.FoodProperty
import com.letsbuildthatapp.foodlocker.network.FoodApi
import com.letsbuildthatapp.foodlocker.utility.ApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "SEARCH FOOD VIEW MODEL"

class SearchFoodViewModel : ViewModel() {

    lateinit var activity: FragmentActivity

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(
            viewModelJob + Dispatchers.Main)

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _foods = MutableLiveData<List<FoodProperty>>()
    val foods: LiveData<List<FoodProperty>>
        get() = _foods

    var searchTextFood: String = ""


    private fun getFoodProperties(foodName: String, view: View) {
        coroutineScope.launch {
            val getPropertiesDeferred = FoodApi.retrofitServiceGetFoodsByName.getFoodByName(foodName)
            try {
                _status.value = ApiStatus.LOADING
                val listResult = getPropertiesDeferred.await()
                _status.value = ApiStatus.DONE
                _foods.value = listResult
                if (listResult.isEmpty())
                    _status.value = ApiStatus.NO_CONTENT
                else
                    Toast.makeText(view.context, "${listResult.size} Results", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Log.e(TAG, "$e")
                _status.value = ApiStatus.ERROR
                _foods.value = ArrayList()
            }
        }
    }

    fun submitFood(view: View) {
        try {
            // hide keyboard on submit
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            Log.e(TAG, "$e")
        }
        getFoodProperties(searchTextFood, view)
    }


}
