package com.letsbuildthatapp.foodlocker.utility

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.models.*
import com.letsbuildthatapp.foodlocker.ui.diary.ExerciseListAdapter
import com.letsbuildthatapp.foodlocker.ui.diary.FoodListAdapter
import com.letsbuildthatapp.foodlocker.ui.diary.searchFood.FoodSearchListAdapter
import com.letsbuildthatapp.foodlocker.ui.diary.searchSport.SportSearchListAdapter
import com.letsbuildthatapp.foodlocker.ui.home.PostListAdapter
import com.letsbuildthatapp.foodlocker.ui.profile.followers.FollowersListAdapter
import com.letsbuildthatapp.foodlocker.ui.profile.followings.FollowingsListAdapter
import com.letsbuildthatapp.foodlocker.ui.profile.myProfile.MyProfilePostListAdapter
import com.letsbuildthatapp.foodlocker.ui.progress.WeightListAdapter
import com.letsbuildthatapp.foodlocker.ui.searchUsers.SearchListAdapter


enum class ApiStatus { LOADING, ERROR, DONE, NO_CONTENT }

/**
 * Binding adapter used to show loading after searching something
 */
@BindingAdapter("loadingStatus")
fun showLoading(view: View, status: Any?) {

    if (status == ApiStatus.LOADING) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

/**
 * Binding adapter used to show no content after searching something
 */
@BindingAdapter("noContentStatus")
fun showNoContent(view: View, status: Any?) {

    if (status == ApiStatus.NO_CONTENT) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

/**
 * Binding adapter used to disable button if network error
 */
@BindingAdapter("buttonStatus")
fun disableButton(view: Button, status: Any?) {
    view.isEnabled = !(status == ApiStatus.ERROR || status == ApiStatus.LOADING)
}


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri =
                imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
                .load(imgUri)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
    }
}


/**
 * Binding adapter used to associate data on recyclerview.
 */

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<PostProperty>?) {

    val adapter = recyclerView.adapter as PostListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listPostMyProfile")
fun bindRecyclerViewPostMyProfile(recyclerView: RecyclerView,
                                  data: List<PostProperty>?) {

    val adapter = recyclerView.adapter as MyProfilePostListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listFollowers")
fun bindRecyclerViewFollowers(recyclerView: RecyclerView,
                              data: List<UserProperty>?) {

    val adapter = recyclerView.adapter as FollowersListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listFollowings")
fun bindRecyclerViewFollowing(recyclerView: RecyclerView,
                              data: List<UserProperty>?) {

    val adapter = recyclerView.adapter as FollowingsListAdapter
    adapter.submitList(data)
}


@BindingAdapter("listDataWeights")
fun bindRecyclerViewWeights(recyclerView: RecyclerView,
                            data: List<WeightProperty>?) {

    val adapter = recyclerView.adapter as WeightListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listDataFood")
fun bindRecyclerViewFood(recyclerView: RecyclerView,
                         data: List<FoodProperty>?) {

    val adapter = recyclerView.adapter as FoodListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listDataExercise")
fun bindRecyclerViewExercise(recyclerView: RecyclerView,
                             data: List<SportProperty>?) {

    val adapter = recyclerView.adapter as ExerciseListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listDataSearchFood")
fun bindRecyclerViewFoodSearch(recyclerView: RecyclerView,
                               data: List<FoodProperty>?) {

    val adapter = recyclerView.adapter as FoodSearchListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listDataSearchSport")
fun bindRecyclerViewSportSearch(recyclerView: RecyclerView,
                                data: List<SportProperty>?) {

    val adapter = recyclerView.adapter as SportSearchListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listDataUsers")
fun bindRecyclerViewUsers(recyclerView: RecyclerView,
                          data: List<UserProperty>?) {

    val adapter = recyclerView.adapter as SearchListAdapter
    adapter.submitList(data)
}

/*
* HOME/POSTS API STATUS
* */
@BindingAdapter("foodLockerApiStatus")
fun bindStatusPost(statusImageView: ImageView,
                   status: Any?) {
    when (status) {
        ApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        ApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        ApiStatus.NO_CONTENT -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_friendship)
        }
        ApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}


@BindingAdapter("bmiStatus")
fun bindStatusBMI(statusTextView: TextView,
                  status: String?) {
    val bmi = status?.toDouble()
    if (bmi != null) {
        if (bmi <= 18.4)
            statusTextView.setTextColor(Color.BLUE)
        if (bmi >= 18.5 && bmi < 25)
            statusTextView.setTextColor(Color.GREEN)
        if (bmi >= 25 && bmi < 30)
            statusTextView.setTextColor(Color.YELLOW)
        if (bmi >= 30)
            statusTextView.setTextColor(Color.RED)
    }
}

@BindingAdapter("caloriesStatus")
fun bindStatusCalories(statusTextView: TextView,
                       calories: String?) {
    val kcal = calories?.toIntOrNull()
    if (kcal != null) {
        if (kcal >= 0)
            statusTextView.setTextColor(Color.GREEN)
        if (kcal < 0)
            statusTextView.setTextColor(Color.RED)
    }
}


@BindingAdapter("date")
fun bindDate(dateText: TextView,
             date: String?) {
    dateText.text = date?.substring(8, 10)
            .plus("/").plus(date?.substring(5, 7))
            .plus("/").plus(date?.take(4))
            .plus("  ").plus(date?.substring(11, 16))
}
