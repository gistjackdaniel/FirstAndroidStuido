// 파일 경로: app/src/main/java/com/example/mytablayoutapp/customUi/gallery/GalleryScreen.kt
package com.example.mytablayoutapp.customUi.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 갤러리 탭에 표시될 화면입니다.
 */
@Composable
fun GalleryScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("나만의 이미지 갤러리 화면")
    }
}