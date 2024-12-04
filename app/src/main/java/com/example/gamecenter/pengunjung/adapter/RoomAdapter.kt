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
import com.example.gamecenter.database.model.Room

class RoomAdapter(
    private val rooms: List<Room>,
    private val onBookClickListener: OnBookClickListener
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    interface OnBookClickListener {
        fun onBookClick(room: Room)
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val roomImage: ImageView = itemView.findViewById(R.id.roomImage)
        private val roomName: TextView = itemView.findViewById(R.id.roomName)
        private val roomDescription: TextView = itemView.findViewById(R.id.roomDescription)
        private val roomPrice: TextView = itemView.findViewById(R.id.roomPrice)
        private val pesanButton: Button = itemView.findViewById(R.id.pesanButton)

        fun bind(room: Room) {
            // Set room details
            roomName.text = room.room_name
            roomDescription.text = room.room_description
            roomPrice.text = "Rp. ${room.room_price} / jam"

            // Load image using Glide
            val imageUrl = "http://10.0.2.2/gamecenter_api/uploads/"
            Glide.with(itemView.context)
                .load(imageUrl + room.image_url)
                .into(roomImage)


            // Set click listener for booking button
            pesanButton.setOnClickListener {
                onBookClickListener.onBookClick(room)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.room_list_item, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size
}