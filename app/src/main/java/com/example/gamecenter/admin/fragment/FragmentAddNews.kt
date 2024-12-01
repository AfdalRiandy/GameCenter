package com.example.gamecenter.admin.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gamecenter.database.api.ApiClient
import com.example.gamecenter.database.model.News
import com.example.gamecenter.database.model.UploadResponse
import com.example.gamecenter.databinding.FragmentAddNewsBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    private val PICK_IMAGE_REQUEST = 1

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

        setupImageSelection()
        setupCreateButton()
        setupBackButton()
    }

    private fun setupImageSelection() {
        binding.imageSelectionCard.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    private fun setupCreateButton() {
        binding.createNewsButton.setOnClickListener {
            val title = binding.NewsNameInput.text.toString()
            val content = binding.NewsDescriptionInput.text.toString()

            if (title.isEmpty() || content.isEmpty() || selectedImageUri == null) {
                Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Upload image first
            val imageFile = File(getRealPathFromURI(selectedImageUri!!))
            val requestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

            // Upload the image and get the image URL
            ApiClient.instance.uploadImage(body).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.imageUrl ?: ""

                        // Create news object
                        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                        val news = News(
                            id = 0, // Server will assign actual ID
                            title = title,
                            content = content,
                            imageUrl = imageUrl, // Use the image URL returned by the API
                            timestamp = timestamp
                        )

                        ApiClient.instance.addNews(news).enqueue(object : retrofit2.Callback<News> {
                            override fun onResponse(call: Call<News>, response: Response<News>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "News created successfully", Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.popBackStack()
                                } else {
                                    Toast.makeText(context, "Failed to create news", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<News>, t: Throwable) {
                                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
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

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            // Update UI to show selected image
            binding.selectedImageView.setImageURI(selectedImageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val cursor = activity?.contentResolver?.query(contentUri, null, null, null, null)
        cursor?.moveToFirst()
        val idx = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val path = cursor?.getString(idx!!)
        cursor?.close()
        return path ?: ""
    }
}
