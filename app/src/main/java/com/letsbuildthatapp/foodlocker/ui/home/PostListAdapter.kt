package com.letsbuildthatapp.foodlocker.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.ListPostViewItemBinding
import com.letsbuildthatapp.foodlocker.models.PostProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.letsbuildthatapp.foodlocker.network.PostApi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "POST LIST ADAPTER"

private var viewModelJob = Job()
private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main)

class PostListAdapter : ListAdapter<PostProperty,
        PostListAdapter.PostPropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostPropertyViewHolder {
        return PostPropertyViewHolder(ListPostViewItemBinding.inflate(
                LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PostPropertyViewHolder, position: Int) {
        val postProperty = getItem(position)
        holder.view.textButton.setOnClickListener {
            coroutineScope.launch {
                val getPropertyDeferred = PostApi.retrofitServiceLikePost.setLike(FirebaseDBMng.auth.currentUser?.uid.toString(), postProperty.id!!)
                try {
                    getItem(position).likes = getPropertyDeferred.await().likes
                    notifyItemChanged(position)
                } catch (e: Exception) {
                    Log.e("adapter", "$e")
                }
            }
        }
        holder.bind(postProperty)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PostProperty>() {
        override fun areItemsTheSame(oldItem: PostProperty, newItem: PostProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PostProperty, newItem: PostProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class PostPropertyViewHolder(var binding: ListPostViewItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        val view = binding

        fun bind(postProperty: PostProperty) {
            binding.property = postProperty

            Log.e(TAG, "$postProperty")

            if (postProperty.photo != null) {
                binding.postImage.isVisible = true
                Picasso.get().load(postProperty.photo).placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image).into(binding.postImage)
            } else
                binding.postImage.isVisible = false
            FirebaseDBMng.showCircularPhoto(postProperty.userId!!, binding.imageviewProfilePost)
            binding.executePendingBindings()
        }
    }

}


