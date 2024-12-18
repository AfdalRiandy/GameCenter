package com.example.gamecenter.pengunjung.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamecenter.R
import com.example.gamecenter.database.model.Food

class FoodAdapter(
    private val food: List<Food>,
    private val onBookClickListener: OnBookClickListener
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    interface OnBookClickListener {
        fun onBookClick(food: Food)
    }

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        private val foodName: TextView = itemView.findViewById(R.id.foodName)
        private val foodDescription: TextView = itemView.findViewById(R.id.foodDescription)
        private val foodPrice: TextView = itemView.findViewById(R.id.foodPrice)
        private val pesanButton: Button = itemView.findViewById(R.id.pesanButton)

        fun bind(food: Food) {
            foodName.text = food.food_name
            foodDescription.text = food.food_description
            foodPrice.text = "Rp. ${food.food_price}"

            val imageUrl = "http://10.0.2.2/gamecenter_api/uploads/"
            Glide.with(itemView.context)
                .load(imageUrl + food.image_url)
                .into(foodImage)

            // Set click listener untuk tombol "Pesan"
            pesanButton.setOnClickListener {
                onBookClickListener.onBookClick(food)
            }
        }
    }

    // Untuk membuat dan mengembalikan FoodViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.food_list_item, parent, false)
        return FoodViewHolder(view)
    }

    // Menghubungkan data ke view holder
    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(food[position])
    }

    // Mendapatkan jumlah item yang ada di adapter
    override fun getItemCount(): Int = food.size
}
