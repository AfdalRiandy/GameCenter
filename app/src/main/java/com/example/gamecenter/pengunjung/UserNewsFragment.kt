package com.example.gamecenter.pengunjung

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.databinding.FragmentPengunjungNewsBinding
import com.example.gamecenter.database.model.NewsResponse
import com.example.gamecenter.R
import com.example.gamecenter.pengunjung.adapter.NewsAdapter
import com.example.gamecenter.database.model.News
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserNewsFragment : Fragment(R.layout.fragment_pengunjung_news) {

    private var _binding: FragmentPengunjungNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter
    private var allNewsList: List<News> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPengunjungNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(parentFragmentManager)
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }

        // Load the news from the API
        loadNews()

        // Set up Search functionality
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                filterNews(query)  // Call the function to filter news based on search query
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed here
            }
        })
    }

    private fun loadNews() {
        ApiClient.instance.getNews().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse != null && newsResponse.success) {
                        val newsList = newsResponse.data
                        allNewsList = newsList ?: emptyList()  // Store the full list
                        if (newsList.isNullOrEmpty()) {
                            binding.emptyStateText.visibility = View.VISIBLE
                        } else {
                            newsAdapter.submitList(newsList)
                            binding.emptyStateText.visibility = View.GONE
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

    // Function to filter the news based on the search query
    private fun filterNews(query: String) {
        val filteredNews = allNewsList.filter { news ->
            news.title.lowercase().contains(query) || news.content.lowercase().contains(query)
        }
        if (filteredNews.isNullOrEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
        } else {
            newsAdapter.submitList(filteredNews)
            binding.emptyStateText.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
