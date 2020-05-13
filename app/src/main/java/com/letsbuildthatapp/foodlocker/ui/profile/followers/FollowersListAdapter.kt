package com.letsbuildthatapp.foodlocker.ui.profile.followers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.databinding.ListFollowersItemBinding
import com.letsbuildthatapp.foodlocker.models.UserProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.UserApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


private const val TAG = "FOLLOWERS ADAPTER"

private var viewModelJob = Job()
private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

class FollowersListAdapter : ListAdapter<UserProperty,
        FollowersListAdapter.UserPropertyViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPropertyViewHolder {
        return UserPropertyViewHolder(ListFollowersItemBinding.inflate(
                LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: UserPropertyViewHolder, position: Int) {
        val userProperty = getItem(position)

        holder.bind(userProperty)

    }

    companion object DiffCallback : DiffUtil.ItemCallback<UserProperty>() {
        override fun areItemsTheSame(oldItem: UserProperty, newItem: UserProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: UserProperty, newItem: UserProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class UserPropertyViewHolder(var binding: ListFollowersItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(userProperty: UserProperty) {
            binding.property = userProperty

            FirebaseDBMng.showCircularPhoto(userProperty.id, binding.imageviewUser)

            binding.remove.setOnClickListener {
                coroutineScope.launch {
                    val getPropertyDeferred = UserApi.retrofitServiceRemoveFollower.removeFollower(FirebaseDBMng.auth.currentUser?.uid.toString(), userProperty.id)
                    try {
                        getPropertyDeferred.await()
                        binding.remove.visibility = View.GONE
                        binding.removedTitle.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Log.e(TAG, "$e")
                    }
                }
            }

            binding.executePendingBindings()
        }
    }


}


