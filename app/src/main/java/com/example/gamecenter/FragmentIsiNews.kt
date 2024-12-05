package com.example.gamecenter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
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

        val title = arguments?.getString("NEWS_TITLE", "")
        val date = arguments?.getString("NEWS_DATE", "")
        val content = arguments?.getString("NEWS_CONTENT", "")
        val imageUrl = arguments?.getString("NEWS_IMAGE_URL", "")

        binding.newsJudul.text = title
        binding.newsTanggal.text = date
        binding.newsContent.text = content

        val fullImageUrl = "http://10.0.2.2/gamecenter_api/uploads/$imageUrl"
        Glide.with(requireContext())
            .load(fullImageUrl)
            .into(binding.newsImage)

        binding.newsImage.setOnClickListener {
            showFullImageDialog(fullImageUrl)
        }

        setupBackButton()

        return binding.root
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showFullImageDialog(imageUrl: String) {
        // Create a custom dialog
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_full_image)

        // Find the ImageView in the dialog
        val fullImageView = dialog.findViewById<ImageView>(R.id.fullImageView)

        // Load the full image into the dialog's ImageView
        Glide.with(requireContext())
            .load(imageUrl)
            .into(fullImageView)

        // Set click listener to dismiss dialog when image is clicked
        fullImageView.setOnClickListener {
            dialog.dismiss()
        }


        // Show the dialog
        dialog.show()

    }
}