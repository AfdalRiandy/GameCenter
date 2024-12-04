package com.example.gamecenter.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecenter.database.model.Room
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.gamecenter.databinding.ItemRoomBinding

class RoomAdapter : ListAdapter<Room, RoomAdapter.RoomViewHolder>(RoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = getItem(position)
        holder.bind(room)
    }

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.roomName.text = room.room_name
            binding.roomDescription.text = room.room_description
            binding.roomPrice.text = "Rp. ${room.room_price}"

            val imageUrl = "http://10.0.2.2/gamecenter_api/uploads/"
            Glide.with(binding.root.context)
                .load(imageUrl + room.image_url)
                .into(binding.roomImage)
        }
    }

    class RoomDiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem == newItem
        }
    }
}