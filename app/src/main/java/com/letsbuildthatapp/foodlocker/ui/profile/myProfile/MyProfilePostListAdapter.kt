package com.letsbuildthatapp.foodlocker.ui.profile.myProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.ListPostMyProfileItemBinding
import com.letsbuildthatapp.foodlocker.models.PostProperty
import com.letsbuildthatapp.foodlocker.network.FirebaseDBMng
import com.squareup.picasso.Picasso

private const val TAG = "POST LIST ADAPTER"

class MyProfilePostListAdapter : ListAdapter<PostProperty,
        MyProfilePostListAdapter.MyProfilePostPropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProfilePostPropertyViewHolder {
        return MyProfilePostPropertyViewHolder(ListPostMyProfileItemBinding.inflate(
                LayoutInflater.from(parent.context)))

    }

    override fun onBindViewHolder(holder: MyProfilePostPropertyViewHolder, position: Int) {
        val postProperty = getItem(position)
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


    class MyProfilePostPropertyViewHolder(var binding: ListPostMyProfileItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(postProperty: PostProperty) {
            binding.property = postProperty
            binding.usernamePost.text = FirebaseDBMng.userDBinfo.value?.username
            if (postProperty.photo != null) {
                Picasso.get().load(postProperty.photo).placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image).into(binding.postImage)
            }
            FirebaseDBMng.showCircularPhoto(postProperty.userId!!, binding.imageviewProfilePost)
            binding.executePendingBindings()
        }
    }


}


