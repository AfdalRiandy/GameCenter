package com.example.gamecenter.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamecenter.R
import com.example.gamecenter.admin.adapter.FoodAdapter
import com.example.gamecenter.admin.fragment.FragmentAddFood
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.FoodResponse
import com.example.gamecenter.databinding.FragmentFoodReservationBinding
import com.google.android.material.button.MaterialButton

class AdminFood : Fragment() {

    private lateinit var binding: FragmentFoodReservationBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var addFoodButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupBackButton()

        // Tombol Add Food
        addFoodButton = binding.addFoodButton
        addFoodButton.setOnClickListener {
            // Pindah ke FragmentAddFood
            val fragment = FragmentAddFood()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)  // Ganti dengan ID container activity Anda
            transaction.addToBackStack(null)  // Menambahkan fragment ke backstack agar bisa kembali
            transaction.commit()
        }

        // Ambil data foods dari API
        loadFoods()
    }


    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter()  // Memperbaiki instansiasi foodAdapter
        binding.FoodRecyclerView.apply {  // Ganti recyclerViewFood ke FoodRecyclerView
            layoutManager = LinearLayoutManager(context)
            adapter = foodAdapter
        }
    }

    private fun loadFoods() {
        ApiClient.instance.getFoods().enqueue(object : retrofit2.Callback<FoodResponse> {
            override fun onResponse(
                call: retrofit2.Call<FoodResponse>,
                response: retrofit2.Response<FoodResponse>
            ) {
                if (response.isSuccessful) {
                    val foodResponse = response.body()
                    if (foodResponse != null && foodResponse.success) {
                        val foodList = foodResponse.data
                        if (!foodList.isNullOrEmpty()) {
                            foodAdapter.submitList(foodList)
                            binding.emptyStateText.visibility = View.GONE
                        } else {
                            binding.emptyStateText.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(context, "Error: ${foodResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load foods", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<FoodResponse>, t: Throwable) {
                Toast.makeText(context, "Error loading foods: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
