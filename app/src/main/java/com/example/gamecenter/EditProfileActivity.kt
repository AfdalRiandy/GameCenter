package com.example.gamecenter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.User
import com.example.gamecenter.database.model.UserResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var editProfileButton: MaterialButton
    private lateinit var backButton: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        // Initialize views
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        editProfileButton = findViewById(R.id.editProfileButton)
        backButton = findViewById(R.id.backButton)
        profileImage = findViewById(R.id.profileImage)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Load current user data
        loadUserData()

        // Set up edit profile button click listener
        editProfileButton.setOnClickListener {
            updateProfile()
        }

        // Set up back button click listener
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set up profile image click listener (for future image upload functionality)
        profileImage.setOnClickListener {
            // TODO: Implement image upload functionality
            Toast.makeText(this, "Image upload coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        val userEmail = sharedPreferences.getString("email", "") ?: return

        ApiClient.instance.getUserData(userEmail).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    val user = response.body()!![0]
                    nameInput.setText(user.full_name)
                    emailInput.setText(user.email)
                } else {
                    Toast.makeText(this@EditProfileActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile() {
        val newName = nameInput.text.toString().trim()
        val userEmail = emailInput.text.toString().trim()

        if (newName.isEmpty()) {
            nameInput.error = "Name cannot be empty"
            return
        }

        ApiClient.instance.updateProfile(userEmail, newName).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@EditProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()

                    // Update the name in SharedPreferences
                    sharedPreferences.edit().putString("full_name", newName).apply()

                    // Finish the activity
                    finish()
                } else {
                    Toast.makeText(this@EditProfileActivity,
                        response.body()?.message ?: "Update failed",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}