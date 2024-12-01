package com.example.gamecenter.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.gamecenter.databinding.FragmentAdminHomeBinding
import com.example.gamecenter.R

class AdminHomeFragment : Fragment() {

    private lateinit var binding: FragmentAdminHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminHomeBinding.inflate(inflater, container, false)

        // Set click listeners for the buttons
        binding.roomMenuButton.setOnClickListener {
            // Navigate to AdminRoom Fragment
            loadFragment(AdminRoom())
        }

        binding.foodMenuButton.setOnClickListener {
            // Navigate to AdminFood Fragment
            loadFragment(AdminFood())
        }

        return binding.root
    }

    private fun loadFragment(fragment: Fragment) {
        // Replace the current fragment with the new one
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // Ensure this ID matches the container ID in your activity layout
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN) // Optional for a smooth transition animation
            .addToBackStack(null) // Optional, to allow the user to navigate back
            .commit()
    }
}
