package com.example.gamecenter.loginregister

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.api.ApiService
import com.example.gamecenter.database.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.gamecenter.R

class RegisterActivity : AppCompatActivity() {

    private val apiService = ApiClient.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi View
        val btnCreateAccount: Button = findViewById(R.id.btnRegister)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etFullName: EditText = findViewById(R.id.etFullName)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val tvLogin: TextView = findViewById(R.id.tvLogin)

        // Set OnClickListener untuk TextView Login
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Set OnClickListener untuk tombol Create Account
        btnCreateAccount.setOnClickListener {
            val emailText = etEmail.text.toString().trim()
            val fullNameText = etFullName.text.toString().trim()
            val passwordText = etPassword.text.toString().trim()

            if (emailText.isNotEmpty() && fullNameText.isNotEmpty() && passwordText.isNotEmpty()) {
                registerUser(emailText, fullNameText, passwordText)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(email: String, fullName: String, password: String) {
        apiService.register(email, fullName, password).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null && userResponse.success) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Navigasi ke aktivitas login
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish() // Tutup RegisterActivity agar tidak kembali dengan tombol back
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            userResponse?.message ?: "Registration failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
