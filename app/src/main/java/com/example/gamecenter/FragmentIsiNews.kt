package com.example.gamecenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gamecenter.databinding.FragmentIsiNewsBinding

class FragmentIsiNews : Fragment() {
    private lateinit var binding: FragmentIsiNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIsiNewsBinding.inflate(inflater, container, false)

        // Retrieve arguments passed from NewsAdapter
        arguments?.let { bundle ->
            val title = bundle.getString("NEWS_TITLE", "")
            val date = bundle.getString("NEWS_DATE", "")
            val content = bundle.getString("NEWS_CONTENT", "")
            val imageUrl = bundle.getString("NEWS_IMAGE_URL", "")

            // Set the retrieved data to views
            binding.newsJudul.text = title
            binding.newsTanggal.text = date
            binding.newsContent.text = content

            // Load image using Glide
            Glide.with(this)
                .load(imageUrl)
                .into(binding.newsImage)
        }

        return binding.root
    }
}