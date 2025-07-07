// file: app/src/main/java/com/example/daejeonpass/data/ReviewComment.kt
package com.example.daejeonpass.data

import java.util.UUID // 고유 ID 생성을 위해

data class ReviewComment(
    val id: String = UUID.randomUUID().toString(), // 각 댓글의 고유 ID
    val reviewId: Int,                             // 어떤 리뷰에 달린 댓글인지 식별
    val authorName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis() // 댓글 작성 시간 (타임스탬프)
    // 필요하다면 프로필 이미지 URL 등 추가 정보 포함 가능
)