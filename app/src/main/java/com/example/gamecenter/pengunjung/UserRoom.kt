package com.example.gamecenter.room

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.Room
import com.example.gamecenter.database.model.RoomResponse
import com.example.gamecenter.pengunjung.adapter.RoomAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRoom : AppCompatActivity(), RoomAdapter.OnBookClickListener {
    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var roomAdapter: RoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_list)

        roomRecyclerView = findViewById(R.id.roomRecyclerView)
        roomRecyclerView.layoutManager = LinearLayoutManager(this)

        val backButton = findViewById<android.widget.ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        fetchRooms()
    }

    private fun fetchRooms() {
        val apiService = ApiClient.instance
        apiService.getRooms().enqueue(object : Callback<RoomResponse> {
            override fun onResponse(call: Call<RoomResponse>, response: Response<RoomResponse>) {
                if (response.isSuccessful) {
                    val roomResponse = response.body()
                    if (roomResponse != null && roomResponse.success) {
                        // Set up RecyclerView with rooms
                        roomResponse.data?.let { rooms ->
                            roomAdapter = RoomAdapter(rooms, this@UserRoom)
                            roomRecyclerView.adapter = roomAdapter
                        } ?: run {
                            Toast.makeText(this@UserRoom, "No rooms found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UserRoom, roomResponse?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UserRoom, "Failed to fetch rooms", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RoomResponse>, t: Throwable) {
                Toast.makeText(this@UserRoom, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBookClick(room: Room) {
        val intent = Intent(this, RoomDetailActivity::class.java).apply {
            putExtra("ROOM_DATA", room)
        }
        startActivity(intent)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}