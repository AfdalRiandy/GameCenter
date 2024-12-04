package com.example.gamecenter.admin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gamecenter.AboutActivity
import com.example.gamecenter.EditProfileActivity
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.User
import com.example.gamecenter.databinding.FragmentSettingsBinding
import com.example.gamecenter.databinding.DialogLogoutBinding
import com.example.gamecenter.loginregister.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileFragment : Fragment(R.layout.fragment_settings) {

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

        // Mengambil email yang disimpan di SharedPreferences setelah login
        val email = getLoggedInUserEmail()

        // Jika email tidak kosong, lakukan pemanggilan API untuk mendapatkan data pengguna
        if (email.isNotEmpty()) {
            loadUserData(email) // Panggil API untuk mendapatkan data pengguna berdasarkan email
        }

        // Set up the logout button click listener
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        // Set up other actions like Edit Profile
        binding.btnAboutapp.setOnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java) // Use requireContext() here
            startActivity(intent)
        }
    }

    private fun loadUserData(email: String) {
        val apiService = ApiClient.instance
        apiService.getUserData(email).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val user = response.body()?.firstOrNull()
                    if (user != null) {
                        updateUI(user)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(user: User) {
        // Set the user's full name
        binding.tvUserName.text = user.full_name
    }

    private fun getLoggedInUserEmail(): String {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_EMAIL", "") ?: ""
    }

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
