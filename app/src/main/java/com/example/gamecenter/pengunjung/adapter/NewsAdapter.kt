package com.example.gamecenter.pengunjung.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamecenter.FragmentIsiNews
import com.example.gamecenter.database.model.News
import com.example.gamecenter.databinding.ItemNewsBinding
import androidx.fragment.app.FragmentManager


class NewsAdapter(
    private val fragmentManager: FragmentManager
) : ListAdapter<News, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            binding.newsTitle.text = news.title
            binding.newsSubtitle.text = news.content
            Glide.with(binding.root.context)
                .load(news.image_url)
                .into(binding.newsImage)
            binding.publishDate.text = news.timestamp

            // Add click listener to Read More button
            binding.readMoreButton.setOnClickListener {
                // Create a new instance of FragmentIsiNews with arguments
                val fragmentIsiNews = FragmentIsiNews().apply {
                    arguments = android.os.Bundle().apply {
                        putString("NEWS_TITLE", news.title)
                        putString("NEWS_DATE", news.timestamp)
                        putString("NEWS_CONTENT", news.content)
                        putString("NEWS_IMAGE_URL", news.image_url)
                    }
                }

                // Navigate to FragmentIsiNews
                fragmentManager.beginTransaction()
                    .replace(com.example.gamecenter.R.id.fragmentContainer, fragmentIsiNews)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
}
