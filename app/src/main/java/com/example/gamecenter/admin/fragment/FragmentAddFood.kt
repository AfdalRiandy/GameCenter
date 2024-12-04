package com.example.gamecenter.admin.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.FoodResponse
import com.example.gamecenter.database.model.UploadResponse
import com.example.gamecenter.databinding.FragmentAddFoodBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FragmentAddFood : Fragment() {

    private lateinit var binding: FragmentAddFoodBinding
    private lateinit var foodNameInput: TextInputEditText
    private lateinit var foodDescriptionInput: TextInputEditText
    private lateinit var foodPriceInput: TextInputEditText
    private lateinit var selectedImageView: ImageView
    private lateinit var createFoodButton: MaterialButton
    private lateinit var uploadImageButton: MaterialButton

    private var selectedImageUrl: String? = null
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate binding
        binding = FragmentAddFoodBinding.inflate(inflater, container, false)

        // Initialize all views
        foodNameInput = binding.FoodNameInput
        foodDescriptionInput = binding.FoodDescriptionInput
        foodPriceInput = binding.FoodPriceInput
        selectedImageView = binding.selectedImageView
        createFoodButton = binding.createFoodButton
        uploadImageButton = binding.uploadImageButton  // Add this line

        // Set up button listeners
        uploadImageButton.setOnClickListener {
            pickImage()
        }

        createFoodButton.setOnClickListener {
            createFood()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                // Set image to ImageView
                selectedImageView.setImageURI(uri)
                uploadImageButton.visibility = View.GONE
                selectedImageView.visibility = View.VISIBLE

                // Upload image
                uploadImage(uri)
            }
        }
    }

    private fun uploadImage(imageUri: Uri?) {
        if (imageUri != null) {
            try {
                // Get the actual file path
                val file = File(getRealPathFromURI(imageUri))

                // Create RequestBody and MultipartBody.Part
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // Call API to upload image
                val apiService = ApiClient.instance
                val call = apiService.uploadImage(body)

                call.enqueue(object : Callback<UploadResponse> {
                    override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            // Save the image URL returned from the server
                            selectedImageUrl = response.body()?.image_url
                            Toast.makeText(context, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: Exception) {
                Toast.makeText(context, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createFood() {
        // Validate inputs
        val foodName = foodNameInput.text.toString().trim()
        val foodDescription = foodDescriptionInput.text.toString().trim()

        // Validate that all fields are filled
        if (foodName.isEmpty() || foodDescription.isEmpty() || foodPriceInput.text.isNullOrEmpty()) {
            Toast.makeText(context, "Harap isi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        val foodPrice = try {
            foodPriceInput.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Harga tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate image is uploaded
        if (selectedImageUrl.isNullOrEmpty()) {
            Toast.makeText(context, "Harap upload gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with food creation
        val apiService = ApiClient.instance
        val call = apiService.addFood(foodName, foodDescription, foodPrice, selectedImageUrl)
        call.enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Makanan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    // Optional: Clear inputs or navigate away
                    clearInputs()
                } else {
                    Toast.makeText(context, "Gagal menambahkan makanan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearInputs() {
        foodNameInput.text?.clear()
        foodDescriptionInput.text?.clear()
        foodPriceInput.text?.clear()
        selectedImageView.setImageURI(null)
        selectedImageView.visibility = View.GONE
        uploadImageButton.visibility = View.VISIBLE
        selectedImageUrl = null
        selectedImageUri = null
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(uri, projection, null, null, null)
        return cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            it.getString(columnIndex)
        } ?: ""
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}