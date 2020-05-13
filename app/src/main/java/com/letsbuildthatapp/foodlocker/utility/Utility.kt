package com.letsbuildthatapp.foodlocker.utility

import android.util.Log
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import java.util.*
import kotlin.math.pow

object Utility {
    // profile info
    var height: Int? = FirebaseDBMng.userDBinfo.value?.height_m
    var gender: String = FirebaseDBMng.userDBinfo.value?.gender.toString()
    var birthday = FirebaseDBMng.userDBinfo.value?.dateOfBirthday.toString()
    var initWeight = FirebaseDBMng.userDBinfo.value?.weight_kg
    var targetWeight = FirebaseDBMng.userDBinfo.value?.target_weight_kg
    var weight: Double = 0.0
    var age: Int = 0
    lateinit var goal: String


    init {
        try {
            age = Calendar.getInstance().get(Calendar.YEAR).minus(birthday.substring(4, 8).toInt())
        } catch (e: Exception) {
            Log.e("UTILITY", "$e")
        }
    }

    fun basalMetabolism(): String {
        return if (gender == "Female") {
            val TA = ((46.322.times(weight)) + (17.744.times(height!!)) + (16.66.times(age)) + 944)
            val HB = (655.095 + (9.5634.times(weight)) + (1.849.times(height!!)) + (4.6756.times(age)))
            (TA.plus(HB)).div(2).toString().take(4)
        } else {
            val HB = (66.4730 + (13.7156.times(weight)) + (5.0033.times(height!!)) + (6.755.times(age)))
            val M = (5 + (10.times(weight)) + (6.25.times(height!!)) + (5.times(age)))
            (M.plus(HB)).div(2).toString().take(4)
        }
    }

    fun computeBMI(): Pair<String, String> {
        val bmiDouble = weight.div((height?.toDouble()?.div(100))?.pow(2)!!)
        var bmiStatus = ""

        if (bmiDouble < 16.5)
            bmiStatus = "Severe Underweight"
        else if (bmiDouble > 16.5 && bmiDouble < 18.4)
            bmiStatus = "Underweight"
        else if (bmiDouble >= 18.5 && bmiDouble < 24.9)
            bmiStatus = "Normal"
        else if (bmiDouble >= 25 && bmiDouble < 30)
            bmiStatus = "Overweight"
        else if (bmiDouble >= 30.1 && bmiDouble < 34.9)
            bmiStatus = "Obesity 1°"
        else if (bmiDouble >= 35 && bmiDouble < 40)
            bmiStatus = "Obesity 2°"
        else if (bmiDouble > 40)
            bmiStatus = "Obesity 3°"

        return Pair(bmiDouble.toString().take(4), bmiStatus)
    }

    fun progress(): String {
        return weight.minus(initWeight!!).toString()
    }

    fun setWeight(weight: String) {
        this.weight = weight.toDouble()
        this.goal = this.weight.minus(targetWeight?.toDouble()!!).toString()
    }



}