package com.letsbuildthatapp.foodlocker.ui.searchUsers

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.databinding.ListUsersItemBinding
import com.letsbuildthatapp.foodlocker.models.UserProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.UserApi
import com.letsbuildthatapp.foodlocker.ui.messages.MessagesActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "SEARCH ADAPTER"

private var viewModelJob = Job()
private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

class SearchListAdapter : ListAdapter<UserProperty,
        SearchListAdapter.UserPropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPropertyViewHolder {
        return UserPropertyViewHolder(ListUsersItemBinding.inflate(
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

    class UserPropertyViewHolder(var binding: ListUsersItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(userProperty: UserProperty) {
            binding.property = userProperty
            if (FirebaseDBMng.followings.find { it.id == userProperty.id } == null)
                binding.followButton.visibility = View.VISIBLE
            else
                binding.messageButton.visibility = View.VISIBLE

            FirebaseDBMng.showCircularPhoto(userProperty.id, binding.userImageProfile)

            binding.followButton.setOnClickListener {
                coroutineScope.launch {
                    val getPropertyDeferred = UserApi.retrofitServiceSetFollowed.setFollowed(FirebaseDBMng.auth.currentUser?.uid.toString(), userProperty.id)
                    try {
                        getPropertyDeferred.await()
                        binding.followButton.visibility = View.GONE
                        binding.messageButton.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Log.e(TAG, "$e")
                    }
                }
            }

            binding.messageButton.setOnClickListener {
                val myIntent = Intent(binding.root.context, MessagesActivity::class.java)
                binding.root.context.startActivity(myIntent)
            }

            binding.executePendingBindings()
        }
    }


}


