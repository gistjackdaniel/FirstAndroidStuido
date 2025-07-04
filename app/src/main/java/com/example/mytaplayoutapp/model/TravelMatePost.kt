
// TravelMatePost.kt (데이터 모델)
package com.example.mytaplayoutapp.model

data class TravelMatePost(
    val id: Int,
    val profileImage: Int, // drawable resource ID
    val title: String,
    val date: String,
    val location: String,
    val price: String,
    val people: String
)