package com.example.gamecenter.pengunjung

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.example.gamecenter.R
import com.example.gamecenter.databinding.FragmentSettingsBinding
import com.example.gamecenter.databinding.DialogLogoutBinding
import com.example.gamecenter.loginregister.LoginActivity

class UserProfileFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the logout button click listener
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        // Set up other actions like Edit Profile
        binding.btnEditProfile.setOnClickListener {
            // Handle Edit Profile action
        }
    }

    // Function to show the logout confirmation dialog
    private fun showLogoutDialog() {
        // Inflate the dialog view
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val dialogBinding = DialogLogoutBinding.bind(dialogView)

        // Create an alert dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Set actions for dialog buttons
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss() // Close the dialog and return to the previous screen
        }

        dialogBinding.btnConfirm.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish() // Optional: Close the current activity to prevent user from going back
        }

        // Show the dialog
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
