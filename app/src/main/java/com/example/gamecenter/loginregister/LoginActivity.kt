package com.example.gamecenter.loginregister

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.api.ApiService
import com.example.gamecenter.database.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.gamecenter.R
import com.example.gamecenter.admin.AdminActivity
import com.example.gamecenter.database.model.User
import com.example.gamecenter.pengunjung.UserActivity

class LoginActivity : AppCompatActivity() {
    companion object {
        const val PREF_NAME = "UserPrefs"
        const val KEY_USER_ID = "USER_ID"
        const val KEY_USER_NAME = "USER_NAME"
        const val KEY_USER_EMAIL = "USER_EMAIL"
        const val KEY_USER_ROLE = "USER_ROLE"
    }

    private val apiService = ApiClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            // Validasi input
            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(emailText, passwordText)
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        apiService.login(email, password).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(
                        this@LoginActivity,
                        "Login failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                val userResponse = response.body()

                if (userResponse?.success == true) {
                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT)
                        .show()

                    // Simpan user ID dari response login
                    val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putInt("USER_ID", userResponse.user_id ?: -1)
                        apply()
                    }

                    // Ambil data user lengkap
                    getUserDataFromApi(email)

                    // Navigasi berdasarkan role
                    val intent = if (userResponse.role == "admin") {
                        Intent(this@LoginActivity, AdminActivity::class.java)
                    } else {
                        Intent(this@LoginActivity, UserActivity::class.java)
                    }

                    intent.putExtra("USER_EMAIL", email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        userResponse?.message ?: "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Menambahkan method untuk mengambil data pengguna berdasarkan email
    private fun getUserDataFromApi(email: String) {
        apiService.getUserData(email).enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val user = response.body()?.firstOrNull()

                    // Simpan data user ke SharedPreferences - KODE BARU
                    val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putInt(KEY_USER_ID, user?.id ?: -1)
                        putString(KEY_USER_NAME, user?.full_name)
                        putString(KEY_USER_EMAIL, user?.email)
                        putString(KEY_USER_ROLE, user?.role)
                        apply()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Failed to fetch user data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error fetching user data: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

