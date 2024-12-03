package com.example.gamecenter.pengunjung

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.model.BookingHistory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserHistoriFragment : Fragment(R.layout.fragment_pengunjung_histori) {
    private lateinit var recyclerViewOrderHistory: RecyclerView
    private lateinit var historiAdapter: HistoriAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewOrderHistory = view.findViewById(R.id.recyclerViewOrderHistory)
        recyclerViewOrderHistory.layoutManager = LinearLayoutManager(context)

        fetchBookingHistory()
    }

    private fun fetchBookingHistory() {
        ApiClient.instance.getBookingHistory().enqueue(object : Callback<List<BookingHistory>> {
            override fun onResponse(
                call: Call<List<BookingHistory>>,
                response: Response<List<BookingHistory>>
            ) {
                if (response.isSuccessful) {
                    val bookingList = response.body() ?: emptyList()
                    if (bookingList.isNotEmpty()) {
                        historiAdapter = HistoriAdapter(bookingList)
                        recyclerViewOrderHistory.adapter = historiAdapter
                    } else {
                        Toast.makeText(context, "No booking history found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch booking history", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<BookingHistory>>, t: Throwable) {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}