// 파일 경로: app/src/main/java/com/example/mytablayoutapp/customUi/contacts/ContactsScreen.kt
package com.example.mytablayoutapp.customUi.contacts // 파일이 위치한 패키지 경로

import androidx.compose.foundation.layout.Box // UI 요소를 담는 컨테이너
import androidx.compose.foundation.layout.fillMaxSize // 화면 전체를 채우도록 크기 지정
import androidx.compose.material3.Text // 텍스트를 표시하는 UI 요소
import androidx.compose.runtime.Composable // Composable 함수임을 나타내는 어노테이션
import androidx.compose.ui.Alignment // Box 내의 컨텐츠 정렬 방식
import androidx.compose.ui.Modifier // UI 요소의 크기, 패딩, 배경 등 속성을 지정

/**
 * 연락처 탭에 표시될 화면입니다.
 * 지금은 간단한 텍스트만 표시합니다.
 */
@Composable
fun ContactsScreen(modifier: Modifier = Modifier) { // modifier를 파라미터로 받아 외부에서 속성 변경 가능
    // Box는 다른 UI 요소들을 담을 수 있는 기본적인 레이아웃 컴포넌트입니다.
    Box(
        modifier = modifier.fillMaxSize(), // 전달받은 modifier에 추가로 전체 화면 크기를 적용
        contentAlignment = Alignment.Center // Box 내부의 내용물을 중앙에 정렬
    ) {
        // Text 컴포넌트를 사용하여 "나의 연락처 화면"이라는 글자를 표시
        Text("나의 연락처 화면")
    }
}