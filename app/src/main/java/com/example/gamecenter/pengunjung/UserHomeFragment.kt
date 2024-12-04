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
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.NewsResponse
import com.example.gamecenter.food.UserFood
import com.example.gamecenter.pengunjung.adapter.NewsAdapter
import com.example.gamecenter.room.UserRoom
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
        val sharedPreferences = requireActivity().getSharedPreferences("GameCenterPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "User")  // Default to "User" if no name found

        // Display the user name
        userNameTextView.text = "$userName"

        // Initialize RecyclerView for news
        val newsRecyclerView = view.findViewById<RecyclerView>(R.id.newsRecyclerView)
        newsRecyclerView.layoutManager = LinearLayoutManager(context)

        // Pass parentFragmentManager when creating NewsAdapter
        newsAdapter = NewsAdapter(parentFragmentManager)
        newsRecyclerView.adapter = newsAdapter

        // Load the news from the API
        loadNews()

        // Room service card listener
        val roomServiceCard = view.findViewById<View>(R.id.roomServiceCard)
        roomServiceCard.setOnClickListener {
            val intent = Intent(requireContext(), UserRoom::class.java)
            startActivity(intent)
        }

        // Food service card listener
        val foodServiceCard = view.findViewById<View>(R.id.foodServiceCard)
        foodServiceCard.setOnClickListener {
            val intent = Intent(requireContext(), UserFood::class.java)
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
                        // Take only the first 3 news items
                        val limitedNewsList = newsResponse.data?.take(3)

                        if (limitedNewsList.isNullOrEmpty()) {
                            // Hide the RecyclerView or show an empty state if no news
                            view?.findViewById<RecyclerView>(R.id.newsRecyclerView)?.visibility = View.GONE
                        } else {
                            // Submit the limited list to the adapter
                            newsAdapter.submitList(limitedNewsList)
                            view?.findViewById<RecyclerView>(R.id.newsRecyclerView)?.visibility = View.VISIBLE
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