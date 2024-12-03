package com.example.gamecenter.room


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.Food
import com.example.gamecenter.database.model.FoodBooking
import com.example.gamecenter.database.model.FoodBookingResponse
import com.example.gamecenter.databinding.FoodDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.google.android.material.button.MaterialButton

class FoodDetailActivity : AppCompatActivity() {
    private lateinit var binding: FoodDetailBinding
    private lateinit var selectedFood: Food
    private var selectedPaymentMethod = "COD"
    private lateinit var selectedDateTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve room data from intent
        selectedFood = intent.getParcelableExtra("FOOD_DATA")!!

        // Setup UI
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Set room details
        binding.foodName.text = selectedFood.food_name
        binding.foodPrice.text = "Rp. ${selectedFood.food_price} "
        binding.foodDescription.text = selectedFood.food_description

        // Load room image
        Glide.with(this)
            .load(selectedFood.image_url)
            .placeholder(R.drawable.card_detail)
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

        // Reservation button
        binding.reserveButton.setOnClickListener {
            showDateTimePicker()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        // Date picker
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Time picker
            val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Format date and time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                selectedDateTime = dateFormat.format(calendar.time)

                // Proceed with booking
                bookFood()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePicker.show()
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    private fun bookFood() {
        val booking = FoodBooking(
            foodId = selectedFood.id,
            foodName = selectedFood.food_name,
            bookingDateTime = selectedDateTime,
            paymentMethod = selectedPaymentMethod,
            totalPrice = selectedFood.food_price
        )

        // Call API to book room
        val apiService = ApiClient.instance
        apiService.bookFood(booking).enqueue(object : Callback<FoodBookingResponse> {
            override fun onResponse(call: Call<FoodBookingResponse>, response: Response<FoodBookingResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(this@FoodDetailActivity, "Booking Failed: ${response.body()?.message}", Toast.LENGTH_LONG).show()
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
        val okButton = dialogView.findViewById<MaterialButton>(R.id.okButton)
        okButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}

