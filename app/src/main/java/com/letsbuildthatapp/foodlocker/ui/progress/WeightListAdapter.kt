package com.letsbuildthatapp.foodlocker.ui.progress

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.R
import com.letsbuildthatapp.foodlocker.databinding.ListWeightItemBinding
import com.letsbuildthatapp.foodlocker.models.WeightProperty
import com.squareup.picasso.Picasso

private const val TAG = "WEIGHT LIST ADAPTER"

class WeightListAdapter : ListAdapter<WeightProperty,
        WeightListAdapter.WeightPropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightPropertyViewHolder {
        return WeightPropertyViewHolder(ListWeightItemBinding.inflate(
                LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: WeightPropertyViewHolder, position: Int) {
        val weightProperty = getItem(position)
        holder.bind(weightProperty)
        if (position + 1 < itemCount) {
            when {
                getItem(position).weight.toDouble() > getItem(position + 1).weight.toDouble() -> holder.binding.fatterAlert.visibility = View.VISIBLE
                getItem(position).weight.toDouble() < getItem(position + 1).weight.toDouble() -> holder.binding.leanAlert.visibility = View.VISIBLE
                getItem(position).weight.toDouble() == getItem(position + 1).weight.toDouble() -> holder.binding.equalAlert.visibility = View.VISIBLE
            }
        } else
            holder.binding.startAlert.visibility = View.VISIBLE
    }


    companion object DiffCallback : DiffUtil.ItemCallback<WeightProperty>() {
        override fun areItemsTheSame(oldItem: WeightProperty, newItem: WeightProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: WeightProperty, newItem: WeightProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }


    class WeightPropertyViewHolder(var binding: ListWeightItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(weightProperty: WeightProperty) {
            binding.property = weightProperty
            Log.e("adapter", "${weightProperty.photo}")

            if (weightProperty.photo != null)
                try {
                    Picasso.get().load(weightProperty.photo).placeholder(R.drawable.loading_animation)
                            .error(R.drawable.ic_broken_image).into(binding.imageProgress)
                } catch (e: Exception) {
                    Log.e(TAG, "$e")
                }

            binding.executePendingBindings()
        }
    }


}


