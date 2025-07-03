// 파일 경로: app/src/main/java/com/example/mytablayoutapp/customUi/freetheme/FreeThemeScreen.kt
package com.example.mytablayoutapp.customUi.freetheme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 자유 주제 탭에 표시될 화면입니다.
 */
@Composable
fun FreeThemeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("자유 주제 화면")
    }
}