package com.example.gamecenter.room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gamecenter.R

class RoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_list)  // Mengarah ke layout room_list.xml

        // Inisialisasi CardView yang berfungsi sebagai Toolbar
        val toolbarCard = findViewById<androidx.cardview.widget.CardView>(R.id.toolbarCard)

        // Menangani klik pada tombol kembali (back button)
        val backButton = findViewById<android.widget.ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()  // Menangani klik tombol kembali
        }
    }

    // Menangani aksi ketika tombol Up ditekan
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()  // Menekan tombol Up akan memanggil onBackPressed()
        return true
    }
}