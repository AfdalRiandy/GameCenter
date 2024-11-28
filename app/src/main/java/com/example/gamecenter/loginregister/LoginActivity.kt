package com.example.gamecenter.loginregister

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecenter.R
import com.example.gamecenter.admin.AdminActivity
import com.example.gamecenter.pengunjung.PengunjungActivity
import com.example.gamecenter.database.ApiClient
import com.example.gamecenter.database.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        ApiClient.instance.loginUser(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val userType = response.body()?.userType
                        when (userType) {
                            "admin" -> {
                                val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                                startActivity(intent)
                            }
                            "pengunjung" -> {
                                val intent = Intent(this@LoginActivity, PengunjungActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, response.body()?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.message ?: "Network error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}