package com.example.gamecenter.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamecenter.admin.adapter.NewsAdapter
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.databinding.FragmentAdminNewsBinding
import com.example.gamecenter.R
import com.example.gamecenter.admin.fragment.FragmentAddNews
import com.example.gamecenter.database.model.News
import com.example.gamecenter.database.model.NewsResponse

class AdminNewsFragment : Fragment() {
    private var _binding: FragmentAdminNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAddButton()
        loadNews()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.NewsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }
    }

    private fun setupAddButton() {
        binding.addNewsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragmentAddNews())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadNews() {
        ApiClient.instance.getNews().enqueue(object : retrofit2.Callback<NewsResponse> { // Change callback type to NewsResponse
            override fun onResponse(
                call: retrofit2.Call<NewsResponse>,
                response: retrofit2.Response<NewsResponse>
            ) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse != null && newsResponse.success) { // Check if response is successful
                        val news = newsResponse.data // Get the list of news from 'data' field
                        if (news != null) {
                            newsAdapter.submitList(news)
                            binding.emptyStateText.visibility =
                                if (news.isEmpty()) View.VISIBLE else View.GONE
                        }
                    } else {
                        Toast.makeText(context, "Error: ${newsResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to load news", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<NewsResponse>, t: Throwable) {
                Toast.makeText(context, "Error loading news: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
