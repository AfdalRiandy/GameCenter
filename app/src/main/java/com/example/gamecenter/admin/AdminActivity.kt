package com.example.gamecenter.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamecenter.R
import com.example.gamecenter.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the default fragment
        loadFragment(AdminHomeFragment())

        // Set up BottomNavigationView
        binding.bottomNavAdmin.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(AdminHomeFragment())
                    true
                }
                R.id.nav_news -> {
                    loadFragment(AdminNewsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(AdminProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}