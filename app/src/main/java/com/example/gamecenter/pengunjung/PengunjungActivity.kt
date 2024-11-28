package com.example.gamecenter.pengunjung

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gamecenter.R
import com.example.gamecenter.databinding.ActivityMainBinding

class PengunjungActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the default fragment
        loadFragment(PengunjungHomeFragment())

        // Set up BottomNavigationView
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    loadFragment(PengunjungHomeFragment())
                    true
                }
                R.id.nav_analysis -> {
                    loadFragment(PengunjungHistoriFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(PengunjungNewsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(PengunjungProfileFragment())
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
