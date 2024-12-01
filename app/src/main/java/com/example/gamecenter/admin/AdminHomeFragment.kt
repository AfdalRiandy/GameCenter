package com.example.gamecenter.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gamecenter.databinding.FragmentAdminHomeBinding
import com.example.gamecenter.R


class AdminHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAdminHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    private fun loadFragment(fragment: Fragment) {
        // Replace the current fragment with the new one
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // Make sure this container ID matches the one in your activity layout
            .addToBackStack(null) // Optional, to allow the user to navigate back
            .commit()
    }
}
