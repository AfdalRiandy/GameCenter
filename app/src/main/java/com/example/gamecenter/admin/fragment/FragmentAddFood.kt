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
    private lateinit var selectImageCard: MaterialCardView
    private lateinit var selectedImageView: ImageView
    private lateinit var createFoodButton: MaterialButton

    private var selectedImageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate binding
        binding = FragmentAddFoodBinding.inflate(inflater, container, false)

        foodNameInput = binding.FoodNameInput
        foodDescriptionInput = binding.FoodDescriptionInput
        foodPriceInput = binding.FoodPriceInput
        selectImageCard = binding.selectImageCard
        selectedImageView = binding.selectedImageView
        createFoodButton = binding.createFoodButton

        selectImageCard.setOnClickListener {
            // Open image picker
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
        // Open image picker functionality
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            selectedImageView.setImageURI(imageUri)
            // Upload image and get the URL
            uploadImage(imageUri)
        }
    }

    private fun uploadImage(imageUri: Uri?) {
        if (imageUri != null) {
            val file = File(getRealPathFromURI(imageUri))
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Corrected reference to ApiClient.instance
            val apiService = ApiClient.instance
            val call = apiService.uploadImage(body)
            call.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                        selectedImageUrl = response.body()?.image_url
                    } else {
                        Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createFood() {
        val foodName = foodNameInput.text.toString()
        val foodDescription = foodDescriptionInput.text.toString()
        val foodPrice = foodPriceInput.text.toString().toDouble()

        // Corrected reference to ApiClient.instance
        val apiService = ApiClient.instance
        val call = apiService.addFood(foodName, foodDescription, foodPrice, selectedImageUrl)
        call.enqueue(object : Callback<FoodResponse> {
            override fun onResponse(call: Call<FoodResponse>, response: Response<FoodResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Food added successfully", Toast.LENGTH_SHORT).show()
                    // Redirect or update UI
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
        val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
        val filePath = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return filePath ?: ""
    }

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }
}
