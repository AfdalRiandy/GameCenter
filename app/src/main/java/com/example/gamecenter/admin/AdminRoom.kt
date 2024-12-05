package com.example.gamecenter.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamecenter.R
import com.example.gamecenter.admin.adapter.RoomAdapter
import com.example.gamecenter.admin.fragment.FragmentAddRoom
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.RoomResponse
import com.example.gamecenter.databinding.FragmentRoomReservationBinding
import com.google.android.material.button.MaterialButton

class AdminRoom : Fragment() {

    private lateinit var binding: FragmentRoomReservationBinding
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var addRoomButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomReservationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBackButton()

        addRoomButton = binding.addRoomButton
        addRoomButton.setOnClickListener {
            val fragment = FragmentAddRoom()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        loadRoom()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        roomAdapter = RoomAdapter()
        binding.recyclerViewRooms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomAdapter
        }
    }

    private fun loadRoom() {
        ApiClient.instance.getRooms().enqueue(object : retrofit2.Callback<RoomResponse> {
            override fun onResponse(
                call: retrofit2.Call<RoomResponse>,
                response: retrofit2.Response<RoomResponse>
            ) {
                if (response.isSuccessful) {
                    val roomResponse = response.body()
                    if (roomResponse != null && roomResponse.success) {
                        val roomList = roomResponse.data
                        if (roomList != null && roomList.isNotEmpty()) {
                            roomAdapter.submitList(roomList)
                            binding.emptyStateText.visibility = View.GONE
                        } else {
                            binding.emptyStateText.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(context, "Error: ${roomResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load rooms", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<RoomResponse>, t: Throwable) {
                Toast.makeText(context, "Error loading rooms: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
