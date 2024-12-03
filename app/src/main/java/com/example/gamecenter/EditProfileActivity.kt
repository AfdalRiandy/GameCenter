package com.example.gamecenter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.User
import com.example.gamecenter.database.model.UserResponse
import com.example.gamecenter.databinding.EditProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: EditProfileBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use View Binding for more efficient view access
        binding = EditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Setup toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Load current user data
        loadUserData()

        // Set up edit profile button click listener
        binding.editProfileButton.setOnClickListener {
            updateProfile()
        }

        // Set up profile image click listener (for future image upload functionality)
        binding.imageCard.setOnClickListener {
            Toast.makeText(this, "Image upload coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        val userEmail = sharedPreferences.getString("email", "") ?: return

        ApiClient.instance.getUserData(userEmail).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    val user = response.body()!![0]
                    binding.nameInput.setText(user.full_name)
                    binding.emailInput.setText(user.email)
                } else {
                    showError("Failed to load user data")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showError("Error: ${t.message}")
            }
        })
    }

    private fun updateProfile() {
        val newName = binding.nameInput.text.toString().trim()
        val userEmail = binding.emailInput.text.toString().trim()

        // Validate inputs
        if (!validateInputs(newName, userEmail)) {
            return
        }

        // Call API to update profile
        ApiClient.instance.updateProfile(userEmail, newName).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update successful
                    updateLocalUserData(newName)
                    showSuccess("Profile updated successfully")
                    finish()
                } else {
                    // Update failed
                    showError(response.body()?.message ?: "Update failed")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                showError("Error: ${t.message}")
            }
        })
    }

    private fun validateInputs(name: String, email: String): Boolean {
        var isValid = true

        // Validate name
        if (name.isEmpty()) {
            binding.nameInputLayout.error = "Name cannot be empty"
            isValid = false
        } else {
            binding.nameInputLayout.error = null
        }

        // Validate email
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email cannot be empty"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Invalid email format"
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        return isValid
    }

    private fun updateLocalUserData(newName: String) {
        // Update SharedPreferences with new name
        sharedPreferences.edit().apply {
            putString("full_name", newName)
        }.apply()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}