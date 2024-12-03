package com.example.gamecenter.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecenter.R
import com.example.gamecenter.database.model.Transaction

class TransaksiAdapter(private val transaksiList: List<Transaction>) :
    RecyclerView.Adapter<TransaksiAdapter.TransaksiViewHolder>() {

    inner class TransaksiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.UserName)
        val description: TextView = itemView.findViewById(R.id.TransaksiDescription)
        val harga: TextView = itemView.findViewById(R.id.Harga)
        val publishDate: TextView = itemView.findViewById(R.id.publishDate)

        fun bind(transaksi: Transaction) {
            userName.text = transaksi.userName
            description.text = transaksi.description
            harga.text = transaksi.totalPrice
            publishDate.text = transaksi.publishDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_transaksi, parent, false)
        return TransaksiViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        holder.bind(transaksiList[position])
    }

    override fun getItemCount() = transaksiList.size
}