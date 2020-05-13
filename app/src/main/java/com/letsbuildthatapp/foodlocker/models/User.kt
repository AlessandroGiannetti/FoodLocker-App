package com.letsbuildthatapp.foodlocker.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String, val dateOfBirthday: String, val gender: String, val height_m: Int, val weight_kg: Int, val target_weight_kg: Int, val water: Int, val weakly_hour_sport: Int, val daily_kcal: Int, val complete: Boolean) : Parcelable {
    constructor() : this("", "", "", "", "", 0, 0, 0, 0, 0, 0, false)
}