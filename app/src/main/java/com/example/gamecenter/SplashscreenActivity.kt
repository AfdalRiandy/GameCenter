package com.example.gamecenter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecenter.loginregister.LoginActivity


class SplashscreenActivity : AppCompatActivity() {
    private val SPLASH_DISPLAY_LENGTH = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splashscreen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)

            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}