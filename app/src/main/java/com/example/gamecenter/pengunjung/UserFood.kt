package com.example.gamecenter.food

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.Food
import com.example.gamecenter.database.model.FoodResponse
import com.example.gamecenter.pengunjung.adapter.FoodAdapter
import com.example.gamecenter.room.FoodDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFood : AppCompatActivity(), FoodAdapter.OnBookClickListener {
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.food_list)

        // Initialize RecyclerView
        foodRecyclerView = findViewById(R.id.foodRecyclerView)
        foodRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Toolbar
        val backButton = findViewById<android.widget.ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Fetch foods from API
        fetchFoods()
    }

    private fun fetchFoods() {
        val apiService = ApiClient.instance
        apiService.getFoods().enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {
                if (response.isSuccessful) {
                    val foodResponse = response.body()
                    if (foodResponse != null && foodResponse.success) {
                        // Set up RecyclerView with foods
                        foodResponse.data?.let { foods ->
                            foodAdapter = FoodAdapter(foods, this@UserFood)
                            foodRecyclerView.adapter = foodAdapter
                        } ?: run {
                            Toast.makeText(this@UserFood, "No foods found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UserFood, foodResponse?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UserFood, "Failed to fetch foods", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                Toast.makeText(this@UserFood, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Implement OnBookClickListener
    override fun onBookClick(food: Food) {
        val intent = Intent(this, FoodDetailActivity::class.java).apply {
            putExtra("FOOD_DATA", food)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
