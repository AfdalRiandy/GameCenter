package com.example.gamecenter.pengunjung

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamecenter.R
import com.example.gamecenter.model.BookingHistory

class HistoriAdapter(private val historiList: List<BookingHistory>) :
    RecyclerView.Adapter<HistoriAdapter.HistoriViewHolder>() {

    inner class HistoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderImage: ImageView = itemView.findViewById(R.id.orderImage)
        val orderDescription: TextView = itemView.findViewById(R.id.orderDescription)
        val orderPrice: TextView = itemView.findViewById(R.id.orderPrice)
        val orderTime: TextView = itemView.findViewById(R.id.orderTime)
        val orderDate: TextView = itemView.findViewById(R.id.orderDate)

        fun bind(booking: BookingHistory) {
            orderDescription.text = "Telah memesan ${booking.roomName}"
            orderPrice.text = "Rp. ${booking.totalPrice}"
            orderTime.text = "${booking.duration} jam"
            orderDate.text = booking.createAt

            val imageUrl = "http://10.0.2.2/gamecenter_api/uploads/"
            Glide.with(itemView.context)
                .load(imageUrl + booking.imageUrl)
                .into(orderImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return HistoriViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoriViewHolder, position: Int) {
        holder.bind(historiList[position])
    }

    override fun getItemCount() = historiList.size
}