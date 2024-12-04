package com.example.gamecenter.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecenter.database.model.Food
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.gamecenter.databinding.ItemFoodBinding

class FoodAdapter : ListAdapter<Food, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = getItem(position)
        holder.bind(food)
    }

    inner class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.foodName.text = food.food_name
            binding.foodDescription.text = food.food_description
            binding.foodPrice.text = "Rp. ${food.food_price}"

            val imageUrl = "http://10.0.2.2/gamecenter_api/uploads/"
            Glide.with(binding.root.context)
                .load(imageUrl + food.image_url)
                .into(binding.foodImage)
        }
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}
