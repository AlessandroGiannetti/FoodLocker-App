package com.letsbuildthatapp.foodlocker.ui.diary

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.databinding.ListDiaryItemBinding
import com.letsbuildthatapp.foodlocker.models.SportProperty

private const val TAG = "EXERCISE ADAPTER"

class ExerciseListAdapter : ListAdapter<SportProperty,
        ExerciseListAdapter.ExercisePropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercisePropertyViewHolder {
        return ExercisePropertyViewHolder(ListDiaryItemBinding.inflate(
                LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ExercisePropertyViewHolder, position: Int) {
        val sportProperty = getItem(position)
        holder.bind(sportProperty)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<SportProperty>() {
        override fun areItemsTheSame(oldItem: SportProperty, newItem: SportProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SportProperty, newItem: SportProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }


    class ExercisePropertyViewHolder(var binding: ListDiaryItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(sportProperty: SportProperty) {
            Log.e(TAG, "$sportProperty")
            binding.sport = sportProperty
            binding.title.text = sportProperty.name
            binding.calories.text = (sportProperty.calories?.toInt()?.times(sportProperty.hour!!)!!).div(60).toString()
            binding.subtitle.text = sportProperty.hour.toString().plus("minutes")


            binding.executePendingBindings()
        }
    }


}


