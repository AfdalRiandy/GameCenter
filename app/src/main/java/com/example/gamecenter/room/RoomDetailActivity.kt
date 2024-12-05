package com.example.gamecenter.room

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gamecenter.R
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.Room
import com.example.gamecenter.database.model.RoomBooking
import com.example.gamecenter.database.model.RoomBookingResponse
import com.example.gamecenter.databinding.RoomDetailBinding
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RoomDetailActivity : AppCompatActivity() {
    private lateinit var binding: RoomDetailBinding
    private lateinit var selectedRoom: Room
    private var selectedDuration = 2
    private var selectedPaymentMethod = "COD"
    private lateinit var selectedDateTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RoomDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedRoom = intent.getParcelableExtra("ROOM_DATA") ?: run {
            Toast.makeText(this, "Room data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        setupListeners()
    }

    private fun calculateTotalPrice(): Double {
        val hourlyRate = selectedRoom.room_price.replace("Rp. ", "")
            .replace(".", "").toDoubleOrNull() ?: 0.0
        return hourlyRate * selectedDuration
    }

    private fun setupUI() {
        binding.roomName.text = selectedRoom.room_name
        binding.roomPrice.text = "Rp. ${selectedRoom.room_price} / jam"
        binding.roomDescription.text = selectedRoom.room_description

        val imageUrl = "http://10.0.2.2/gamecenter_api/uploads/"
        Glide.with(this)
            .load(imageUrl + selectedRoom.image_url)
            .into(binding.roomImage)

        binding.backButton.setOnClickListener { onBackPressed() }
    }

    private fun setupListeners() {
        binding.packageOptions.setOnCheckedChangeListener { _, checkedId ->
            selectedDuration = when (checkedId) {
                R.id.package1Hour -> 1
                R.id.package2Hour -> 2
                R.id.package3Hour -> 3
                else -> 2
            }
        }

        binding.paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.codOption -> "COD"
                R.id.comingSoonOption -> "Coming Soon"
                else -> "COD"
            }
        }

        binding.reserveButton.setOnClickListener { showDateTimePicker() }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                selectedDateTime = dateFormat.format(calendar.time)

                bookRoom()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

            timePicker.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    private fun bookRoom() {
        val booking = RoomBooking(
            roomId = selectedRoom.id,
            roomName = selectedRoom.room_name,
            duration = selectedDuration,
            bookingDateTime = selectedDateTime,
            paymentMethod = selectedPaymentMethod,
            totalPrice = calculateTotalPrice()
        )

        ApiClient.instance.bookRoom(booking).enqueue(object : Callback<RoomBookingResponse> {
            override fun onResponse(call: Call<RoomBookingResponse>, response: Response<RoomBookingResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(this@RoomDetailActivity, "Booking Failed: ${response.body()?.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RoomBookingResponse>, t: Throwable) {
                Toast.makeText(this@RoomDetailActivity, "Network Error: ${t.message}", Toast.LENGTH_LONG).show()
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
            finish()
        }
        dialog.show()
    }
}
