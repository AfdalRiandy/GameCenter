package com.example.gamecenter.admin.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.News
import com.example.gamecenter.database.model.NewsResponse
import com.example.gamecenter.database.model.UploadResponse
import com.example.gamecenter.databinding.FragmentAddNewsBinding
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FragmentAddNews : Fragment() {
    private var _binding: FragmentAddNewsBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private var selectedImageUrl: String? = null
    private val PICK_IMAGE_REQUEST = 1
    private val STORAGE_PERMISSION_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImageUpload()
        setupCreateButton()
        setupBackButton()
        checkStoragePermission()
    }

    private fun setupImageUpload() {
        binding.uploadImageButton.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data

            // Use Glide to load the image
            selectedImageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(binding.selectedImageView)

                binding.uploadImageButton.visibility = View.GONE
                binding.selectedImageView.visibility = View.VISIBLE

                // Upload the image
                uploadImage(uri)
            }
        }
    }

    private fun uploadImage(imageUri: Uri?) {
        imageUri?.let { uri ->
            val file = File(getRealPathFromURI(uri))
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val apiService = ApiClient.instance
            apiService.uploadImage(body).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        selectedImageUrl = response.body()?.image_url
                        Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Toast.makeText(context, "Image upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupCreateButton() {
        binding.createNewsButton.setOnClickListener {
            val title = binding.NewsNameInput.text.toString()
            val content = binding.NewsDescriptionInput.text.toString()

            if (title.isEmpty() || content.isEmpty() || selectedImageUrl.isNullOrEmpty()) {
                Toast.makeText(context, "Please fill all fields and upload an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create news with the uploaded image URL
            createNews(selectedImageUrl!!)
        }
    }

    private fun createNews(imageUrl: String) {
        val news = News(
            id = 0,
            title = binding.NewsNameInput.text.toString(),
            content = binding.NewsDescriptionInput.text.toString(),
            image_url = imageUrl,
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        ApiClient.instance.addNews(news).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val newsResponse = response.body()
                    if (newsResponse?.success == true) {
                        Toast.makeText(context, "News created successfully", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to create news: ${newsResponse?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Server error: ${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(contentUri, projection, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val path = cursor?.getString(columnIndex ?: 0)
        cursor?.close()
        return path ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}