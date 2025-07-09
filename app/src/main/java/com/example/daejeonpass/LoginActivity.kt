package com.example.daejeonpass

import android.R.attr.type
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import com.example.DaejeonPass.R
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.daejeonpass.model.UserViewModel
import kotlinx.coroutines.selects.select
import java.io.File
import java.io.FileOutputStream

class LoginActivity : AppCompatActivity (
){
    private lateinit var editNickname: EditText
    private lateinit var editAge: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var buttonSelectImage: Button
    private lateinit var imageProfile: ImageView
    private lateinit var loginSubmit: Button

    private var selectedImageUri: Uri? = null

    companion object {
        const val REQUEST_PICK_IMAGE = 1001           // 갤러리 요청 코드
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry_login)

        val userViewModel = (application as MyApplication).userViewModel

        editNickname = findViewById(R.id.editNickname)
        editAge = findViewById(R.id.editAge)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)
        imageProfile = findViewById(R.id.imageProfile)
        loginSubmit = findViewById(R.id.LoginSubmit)

        buttonSelectImage.setOnClickListener {
            // 이미지 선택 로직 구현
            val intent = Intent(Intent.ACTION_PICK).apply{
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }

        loginSubmit.setOnClickListener {
            val nickname = editNickname.text.toString().trim()
            val age = editAge.text.toString().toIntOrNull()
            val gender = when (radioGroupGender.checkedRadioButtonId) {
                R.id.radioMale -> "남"
                R.id.radioFemale -> "여"
                else -> ""

            }

            if(age==null || age!in 1..100){
                Toast.makeText(this, "나이를 1~100 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(nickname.isEmpty()||gender.isEmpty()){
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("LoginActivity", "Selected URI: $selectedImageUri")

            val finalUri = selectedImageUri?.let { uri ->
                val copied = copyImageToCache(uri)
                Log.d("LoginActivity", "Copied URI: $copied")
                copied
            } ?: getDefaultProfileImageUri()

            Log.d("LoginActivity", "최종 URI: $finalUri")

            userViewModel.setUserInfo(
                nickname = nickname,
                age = age,
                gender = gender,
                profileUri = finalUri
            )
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
            imageProfile.setImageURI(selectedImageUri)  // 선택한 이미지 표시
        }
    }

//    private fun uriToBitmap(uri:Uri):Bitmap?{
//        return try{
//            contentResolver.openInputStream(uri)?.use { input ->
//                BitmapFactory.decodeStream(input)}
//        } catch (e:Exception){
//            e.printStackTrace()
//            null
//        }
//    }

    @SuppressLint("ResourceType")
    private fun getDefaultProfileImageUri(): Uri {
        val file = File(cacheDir, "basic_profile.png")

        if(!file.exists()){
            val inputStream = resources.openRawResource(R.drawable.basic_profile)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        }

        return FileProvider.getUriForFile(
            this,
            "com.example.daejeonpass.fileprovider",
            file
        )
    }

    private fun copyImageToCache(uri: Uri): Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e("copyImageToCache", "inputStream is null")
                return null
            }

            val file = File(cacheDir, "user_selected_image.png")
            val outputStream = FileOutputStream(file)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            val uriResult = FileProvider.getUriForFile(
                this,
                "com.example.daejeonpass.fileprovider",
                file
            )
            Log.d("copyImageToCache", "copied URI: $uriResult")

            uriResult
        } catch (e: Exception) {
            Log.e("copyImageToCache", "Exception: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}