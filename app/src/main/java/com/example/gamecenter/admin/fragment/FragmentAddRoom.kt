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
import com.example.gamecenter.database.model.RoomResponse
import com.example.gamecenter.database.model.UploadResponse
import com.example.gamecenter.databinding.FragmentAddRoomBinding
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

class FragmentAddRoom : Fragment() {

    private lateinit var binding: FragmentAddRoomBinding
    private lateinit var roomNameInput: TextInputEditText
    private lateinit var roomDescriptionInput: TextInputEditText
    private lateinit var roomPriceInput: TextInputEditText
    private lateinit var selectedImageView: ImageView
    private lateinit var createRoomButton: MaterialButton
    private lateinit var uploadImageButton: MaterialButton

    private var selectedImageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate binding
        binding = FragmentAddRoomBinding.inflate(inflater, container, false)

        roomNameInput = binding.roomNameInput
        roomDescriptionInput = binding.roomDescriptionInput
        roomPriceInput = binding.roomPriceInput
        selectedImageView = binding.selectedImageView
        createRoomButton = binding.createRoomButton
        uploadImageButton = binding.uploadImageButton

        uploadImageButton.setOnClickListener {
            pickImage()
        }

        createRoomButton.setOnClickListener {
            createRoom()
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
            uploadImageButton.visibility = View.GONE
            selectedImageView.visibility = View.VISIBLE
            uploadImage(imageUri)
        }
    }

    private fun uploadImage(imageUri: Uri?) {
        if (imageUri != null) {
            val file = File(getRealPathFromURI(imageUri))
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val apiService = ApiClient.instance
            val call = apiService.uploadImage(body)
            call.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        // Simpan nama file sebagai URL gambar
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
            Toast.makeText(context, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createRoom() {
        val roomName = roomNameInput.text.toString()
        val roomDescription = roomDescriptionInput.text.toString()
        val roomPrice = roomPriceInput.text.toString().toDouble()

        // Corrected reference to ApiClient.instance
        val apiService = ApiClient.instance
        val call = apiService.addRoom(roomName, roomDescription, roomPrice, selectedImageUrl)
        call.enqueue(object : Callback<RoomResponse> {
            override fun onResponse(call: Call<RoomResponse>, response: Response<RoomResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Room added successfully", Toast.LENGTH_SHORT).show()
                    // Redirect or update UI
                } else {
                    Toast.makeText(context, "Failed to add room", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RoomResponse>, t: Throwable) {
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
