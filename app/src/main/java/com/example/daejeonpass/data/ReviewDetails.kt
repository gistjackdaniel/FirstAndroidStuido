package com.example.daejeonpass.data

import android.net.Uri

// ReviewDetailScreen에서 사용할 상세 리뷰 정보
data class ReviewDetails(
    val reviewId: Int,
    val title: String,
    val content: String,
    val authorName: String,
    val profileImageUri: Uri?, // 작성자 프로필 이미지
    val reviewImageRes: String,  // 리뷰 대표 이미지 (썸네일과 같을 수도, 다를 수도 있음)
    val date: String,
    val rating: Float
    // 필요에 따라 더 많은 필드 추가
)