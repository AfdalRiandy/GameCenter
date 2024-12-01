package com.example.gamecenter.pengunjung

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiService
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.News
import com.example.gamecenter.database.model.NewsResponse
import com.example.gamecenter.pengunjung.adapter.NewsAdapter
import com.example.gamecenter.food.FoodActivity
import com.example.gamecenter.room.RoomActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserHomeFragment : Fragment(R.layout.fragment_pengunjung_home) {

    private lateinit var userNameTextView: TextView
    private lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the user name TextView
        userNameTextView = view.findViewById(R.id.userName)

        // Get the user name from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("LuminaryPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "User")  // Default to "User" if no name found

        // Display the user name
        userNameTextView.text = "$userName"

        // Initialize RecyclerView for news
        val newsRecyclerView = view.findViewById<RecyclerView>(R.id.newsRecyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(context)
        newsAdapter = NewsAdapter()
        newsRecyclerView.adapter = newsAdapter

        // Load the news from the API
        loadNews()

        // Room service card listener
        val roomServiceCard = view.findViewById<View>(R.id.roomServiceCard)
        roomServiceCard.setOnClickListener {
            val intent = Intent(requireContext(), RoomActivity::class.java)
            startActivity(intent)
        }

        // Food service card listener
        val foodServiceCard = view.findViewById<View>(R.id.foodServiceCard)
        foodServiceCard.setOnClickListener {
            val intent = Intent(requireContext(), FoodActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to load news from the API with a limit of 3 items
    private fun loadNews() {
        ApiClient.instance.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse != null && newsResponse.success) {
                        val newsList = newsResponse.data
                        if (newsList.isNullOrEmpty()) {
                            // Show a message if no news is available
                            view?.findViewById<View>(R.id.emptyStateText)?.visibility = View.VISIBLE
                        } else {
                            // Limit to the first 3 news items
                            val limitedNewsList = newsList.take(3)
                            newsAdapter.submitList(limitedNewsList)
                            view?.findViewById<View>(R.id.emptyStateText)?.visibility = View.GONE
                        }
                    } else {
                        Toast.makeText(context, "Error: ${newsResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load news", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(context, "Error loading news: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
