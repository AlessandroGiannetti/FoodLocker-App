package com.letsbuildthatapp.foodlocker.ui.diary

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.databinding.ListDiaryItemBinding
import com.letsbuildthatapp.foodlocker.models.FoodProperty

private const val TAG = "FOOD ADAPTER"

class FoodListAdapter : ListAdapter<FoodProperty,
        FoodListAdapter.FoodPropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodPropertyViewHolder {
        return FoodPropertyViewHolder(ListDiaryItemBinding.inflate(
                LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: FoodPropertyViewHolder, position: Int) {
        val foodProperty = getItem(position)
        holder.bind(foodProperty)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<FoodProperty>() {
        override fun areItemsTheSame(oldItem: FoodProperty, newItem: FoodProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FoodProperty, newItem: FoodProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }


    class FoodPropertyViewHolder(var binding: ListDiaryItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(foodProperty: FoodProperty) {
            Log.e(TAG, "$foodProperty")
            binding.food = foodProperty
            binding.title.text = foodProperty.name
            binding.calories.text = foodProperty.energy_kcal
            binding.subtitle.text = foodProperty.gmwt_desc1

            binding.executePendingBindings()
        }
    }


}


