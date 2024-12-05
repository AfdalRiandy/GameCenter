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
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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

    private fun createRoom() {
        val roomName = roomNameInput.text.toString().trim()
        val roomDescription = roomDescriptionInput.text.toString().trim()
        val roomPriceText = roomPriceInput.text.toString().trim()

        if (roomName.isEmpty()) {
            roomNameInput.error = "Nama room tidak boleh kosong"
            roomNameInput.requestFocus()
            return
        }

        if (roomDescription.isEmpty()) {
            roomDescriptionInput.error = "Deskripsi room tidak boleh kosong"
            roomDescriptionInput.requestFocus()
            return
        }

        if (roomPriceText.isEmpty()) {
            roomPriceInput.error = "Harga room tidak boleh kosong"
            roomPriceInput.requestFocus()
            return
        }

        val roomPrice: Double
        try {
            roomPrice = roomPriceText.toDouble()
        } catch (e: NumberFormatException) {
            roomPriceInput.error = "Harga harus berupa angka"
            roomPriceInput.requestFocus()
            return
        }

        if (selectedImageUrl == null) {
            Toast.makeText(context, "Harap unggah gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.instance
        val call = apiService.addRoom(roomName, roomDescription, roomPrice, selectedImageUrl)
        call.enqueue(object : Callback<RoomResponse> {
            override fun onResponse(call: Call<RoomResponse>, response: Response<RoomResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "Room added successfully", Toast.LENGTH_SHORT).show()
                    // Navigasi kembali ke fragment sebelumnya
                    requireActivity().onBackPressed()
                    // Atau menggunakan popBackStack
                    // parentFragmentManager.popBackStack()
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
