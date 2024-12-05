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
        binding.roomMenuButton.setOnClickListener {
            loadFragment(AdminRoom())
        }
        binding.foodMenuButton.setOnClickListener {
            loadFragment(AdminFood())
        }
        return binding.root
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commit()
    }
}
