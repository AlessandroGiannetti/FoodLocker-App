package com.letsbuildthatapp.foodlocker.ui.diary.searchSport

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.databinding.ListSearchSportItemBinding
import com.letsbuildthatapp.foodlocker.models.SportProperty
import com.letsbuildthatapp.foodlocker.ui.diary.addSport.AddSportActivity

private const val TAG = "FOOD ADAPTER"

lateinit var parentNavController: NavController

class SportSearchListAdapter() : ListAdapter<SportProperty,
        SportSearchListAdapter.SportPropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportPropertyViewHolder {
        parentNavController = parent.findNavController()
        return SportPropertyViewHolder(ListSearchSportItemBinding.inflate(
                LayoutInflater.from(parent.context)))

    }

    override fun onBindViewHolder(holder: SportPropertyViewHolder, position: Int) {
        val foodProperty = getItem(position)
        holder.bind(foodProperty)
    }


    companion object DiffCallback : DiffUtil.ItemCallback<SportProperty>() {
        override fun areItemsTheSame(oldItem: SportProperty, newItem: SportProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: SportProperty, newItem: SportProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }


    class SportPropertyViewHolder(var binding: ListSearchSportItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(sportProperty: SportProperty) {
            binding.sport = sportProperty
            binding.searchSportItem.setOnClickListener {
                val myIntent = Intent(binding.root.context, AddSportActivity::class.java)
                myIntent.putExtra("sportId", sportProperty.id.toString())
                binding.root.context.startActivity(myIntent)
            }

            binding.executePendingBindings()
        }
    }


}


