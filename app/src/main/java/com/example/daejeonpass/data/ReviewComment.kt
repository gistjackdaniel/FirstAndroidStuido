// file: app/src/main/java/com/example/daejeonpass/data/ReviewComment.kt
package com.example.daejeonpass.data

import android.net.Uri
import java.util.UUID // 고유 ID 생성을 위해

data class ReviewComment(
    val Id: String = UUID.randomUUID().toString(), // 생성 시 자동으로 고유 ID 할당
    val reviewId: Int, // 어떤 리뷰에 달린 댓글인지
    val authorName: String,
    val content: String,
    val timestamp: Long, // 댓글 작성 시간
    val profileImageUri: Uri? // 댓글 작성자 프로필 이미지 (임시)
)