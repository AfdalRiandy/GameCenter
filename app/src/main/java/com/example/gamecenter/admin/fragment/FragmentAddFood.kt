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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFoodBinding.inflate(inflater, container, false)

        foodNameInput = binding.FoodNameInput
        foodDescriptionInput = binding.FoodDescriptionInput
        foodPriceInput = binding.FoodPriceInput
        selectedImageView = binding.selectedImageView
        createFoodButton = binding.createFoodButton
        uploadImageButton = binding.uploadImageButton

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
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            selectedImageView.setImageURI(imageUri)
            uploadImageButton.visibility = View.GONE
            selectedImageView.visibility = View.VISIBLE
            uploadImage(imageUri)
        }
    }

    private fun uploadImage(imageUri: Uri?) {
        if (imageUri != null) {
            val filePath = getRealPathFromURI(imageUri)
            if (filePath.isNotEmpty()) {
                val file = File(filePath)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val apiService = ApiClient.instance
                val call = apiService.uploadImage(body)
                call.enqueue(object : Callback<UploadResponse> {
                    override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
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
            } else {
                Toast.makeText(context, "Path gambar tidak valid", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createFood() {
        val foodName = foodNameInput.text.toString().trim()
        val foodDescription = foodDescriptionInput.text.toString().trim()
        val foodPriceText = foodPriceInput.text.toString().trim()

        if (foodName.isEmpty()) {
            foodNameInput.error = "Nama food tidak boleh kosong"
            foodNameInput.requestFocus()
            return
        }

        if (foodDescription.isEmpty()) {
            foodDescriptionInput.error = "Deskripsi food tidak boleh kosong"
            foodDescriptionInput.requestFocus()
            return
        }

        if (foodPriceText.isEmpty()) {
            foodPriceInput.error = "Harga food tidak boleh kosong"
            foodPriceInput.requestFocus()
            return
        }

        val foodPrice: Double
        try {
            foodPrice = foodPriceText.toDouble()
        } catch (e: NumberFormatException) {
            foodPriceInput.error = "Harga harus berupa angka"
            foodPriceInput.requestFocus()
            return
        }

        if (selectedImageUrl == null) {
            Toast.makeText(context, "Harap unggah gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.instance
        val call = apiService.addFood(foodName, foodDescription, foodPrice, selectedImageUrl)
        call.enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "Food added successfully", Toast.LENGTH_SHORT).show()
                    // Navigasi kembali ke fragment sebelumnya
                    requireActivity().onBackPressed()
                    // Atau menggunakan popBackStack
                    // parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Failed to add food", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var realPath = ""
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = it.getString(columnIndex)
            }
        }
        return realPath
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}
