package com.example.gamecenter.room

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.Food
import com.example.gamecenter.database.model.FoodBooking
import com.example.gamecenter.database.model.FoodBookingResponse
import com.example.gamecenter.databinding.FoodDetailBinding
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var binding: FoodDetailBinding
    private lateinit var selectedFood: Food
    private var selectedPaymentMethod = "COD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get food data from intent
        selectedFood = intent.getParcelableExtra("FOOD_DATA") ?: run {
            Toast.makeText(this, "Data makanan tidak tersedia", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.foodName.text = selectedFood.food_name
        binding.foodPrice.text = "Rp. ${selectedFood.food_price}"
        binding.foodDescription.text = selectedFood.food_description

        // Load food image
        val imageUrl = "http://10.0.2.2/gamecenter_api/uploads/"
        Glide.with(this)
            .load(imageUrl + selectedFood.image_url)
            .into(binding.foodImage)

        // Setup back button
        binding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun setupListeners() {
        // Payment method selection
        binding.paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.codOption -> "COD"
                R.id.comingSoonOption -> "Coming Soon"
                else -> "COD"
            }
        }

        // Reserve button
        binding.reserveButton.setOnClickListener {
            bookFood()
        }
    }

    private fun bookFood() {
        // Get current date and time
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val selectedDateTime = dateFormat.format(calendar.time)

        val totalPrice = selectedFood.food_price.replace("Rp. ", "").replace(".", "").toDoubleOrNull()
            ?: run {
                Toast.makeText(this, "Harga makanan tidak valid", Toast.LENGTH_SHORT).show()
                return
            }

        val booking = FoodBooking(
            foodId = selectedFood.id,
            foodName = selectedFood.food_name,
            bookingDateTime = selectedDateTime,
            paymentMethod = selectedPaymentMethod,
            totalPrice = totalPrice
        )

        ApiClient.instance.bookFood(booking).enqueue(object : Callback<FoodBookingResponse> {
            override fun onResponse(call: Call<FoodBookingResponse>, response: Response<FoodBookingResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    showSuccessDialog()
                } else {
                    val errorMessage = response.body()?.message ?: "Booking gagal"
                    Toast.makeText(this@FoodDetailActivity, "Booking Failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<FoodBookingResponse>, t: Throwable) {
                Toast.makeText(this@FoodDetailActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.success_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.findViewById<MaterialButton>(R.id.okButton).setOnClickListener {
            dialog.dismiss()
            finish() // Return to the previous activity
        }
        dialog.show()
    }
}
