package com.example.daejeonpass.model

import android.graphics.Bitmap
import android.net.Uri
import com.example.DaejeonPass.R
// TravelMatePost.kt (데이터 모델)
data class TravelMatePost(
    val id:Int,
    val profileImage: Uri?, // drawable resource ID
    val authorName: String,
    val gender: String,
    val age: Int,

    val title: String,
    val date: String,
    val location: String,
    val price: Int,
    val totalpeople: Int,
    val currentpeople: Int = 1,
    val gita: String,

    val isBookmarked: Boolean,
    val participants: List<UserProfile> = emptyList()
)

data class UserProfile(
    val profileImage: Uri?,
    val name: String = "정보없음",
    val gender: String ="?",
    val age: Int = 0
)