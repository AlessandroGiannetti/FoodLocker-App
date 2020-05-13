package com.letsbuildthatapp.foodlocker.ui.diary.searchFood

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.letsbuildthatapp.foodlocker.databinding.ListSearchFoodItemBinding
import com.letsbuildthatapp.foodlocker.models.FoodProperty
import com.letsbuildthatapp.foodlocker.ui.diary.addFood.AddFoodActivity

private const val TAG = "FOOD ADAPTER"

lateinit var parentNavController: NavController
lateinit var meal: String

class FoodSearchListAdapter(val mealType: String) : ListAdapter<FoodProperty,
        FoodSearchListAdapter.FoodPropertyViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodPropertyViewHolder {
        parentNavController = parent.findNavController()
        meal = mealType
        return FoodPropertyViewHolder(ListSearchFoodItemBinding.inflate(
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


    class FoodPropertyViewHolder(var binding: ListSearchFoodItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(foodProperty: FoodProperty) {
            binding.food = foodProperty
            binding.searchFoodItem.setOnClickListener {
                val myIntent = Intent(binding.root.context, AddFoodActivity::class.java)
                myIntent.putExtra("foodId", foodProperty.id.toString())
                myIntent.putExtra("mealType", meal)
                binding.root.context.startActivity(myIntent)
            }

            binding.executePendingBindings()
        }
    }


}


