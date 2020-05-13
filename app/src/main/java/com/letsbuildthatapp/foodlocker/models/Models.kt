package com.letsbuildthatapp.foodlocker.models

import com.squareup.moshi.Json

data class UserProperty(
        val id: String,
        val username: String?,
        @Json(name = "photo_profile") val photoProfile: String?)

data class WeightProperty(
        val id: String,
        val weight: String,
        val photo: String?,
        @Json(name = "created_at") val createdAt: String?)

data class FollowerProperty(
        val id: String,
        @Json(name = "followed_id") val followedId: String?)

data class DayProperty(
        val id: Int?,
        val date: String?,
        val water: Int?,
        var note: String?,
        @Json(name = "diary_id") val diaryId: String?)

data class PostProperty(
        val id: Int?,
        val content: String?,
        val photo: String?,
        var likes: Int?,
        val username: String?,
        @Json(name = "created_at") val createdAt: String?,
        @Json(name = "user_id") val userId: String?,
        @Json(name = "photo_profile") val photoProfile: String?
)

data class SportProperty(
        val id: Int?,
        val name: String?,
        val calories: String?,
        var hour: Int?,
        @Json(name = "created_at") val createdAt: String?,
        @Json(name = "sport_id") val sportId: Int?,
        @Json(name = "day_id") val dayId: Int?)

data class FoodProperty(
        val id: Int?,
        val name: String?,
        val meal: Int?,
        val water_g: String?,
        val energy_kcal: String?,
        val protein_g: String?,
        val lipid_tot_g: String?,
        val ash_g: String?,
        val carbohydrt_g: String?,
        val fiber_td_g: String?,
        val sugar_tot_g: String?,
        val calcium_mg: String?,
        val iron_mg: String?,
        val magnesium_mg: String?,
        val phosphorus_mg: String?,
        val potassium_mg: String?,
        val sodium_mg: String?,
        val zinc_mg: String?,
        val copper_mg: String?,
        val manganese_mg: String?,
        val selenium_µg: String?,
        val vit_c_mg: String?,
        val thiamin_mg: String?,
        val riboflavin_mg: String?,
        val niacin_mg: String?,
        val panto_acid_mg: String?,
        val vit_b6_mg: String?,
        val folate_tot_µg: String?,
        val folic_acid_µg: String?,
        val food_folate_µg: String?,
        val folate_dfe_µg: String?,
        val choline_tot_mg: String?,
        val vit_b12: String?,
        val vit_a_i: String?,
        val vit_a_rae: String?,
        val retinol_µg: String?,
        val alpha_carot_µg: String?,
        val beta_carot_µg: String?,
        val beta_crypt_µg: String?,
        val lycopene_µg: String?,
        val lut_zea_µg: String?,
        val vit_e_mg: String?,
        val vit_d: String?,
        val vit_d_i: String?,
        val vit_k_µg: String?,
        val fa_sat_g: String?,
        val fa_mono_g: String?,
        val fa_poly_g: String?,
        val cholestrl_mg: String?,
        val gmwt: String?,
        val gmwt_desc1: String?
)