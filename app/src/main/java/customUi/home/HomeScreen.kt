package com.example.mytaplayoutapp.customUi.home

// ✅ 필요한 Compose UI 컴포넌트 및 라이브러리 import
import androidx.compose.foundation.layout.fillMaxSize  // 전체 화면 크기 설정을 위한 Modifier
import androidx.compose.foundation.lazy.LazyColumn      // 스크롤 가능한 리스트 구현을 위한 LazyColumn
import androidx.compose.foundation.lazy.items          // 리스트 항목 반복 출력용 items
import androidx.compose.material.icons.Icons           // 기본 제공 아이콘 세트
import androidx.compose.material.icons.filled.FilterList // 필터 아이콘 (상단 필터 버튼용)
import androidx.compose.material.icons.filled.Notifications // 알림 아이콘 (상단 팝업/하단 임시 아이콘)
import androidx.compose.material3.*                     // Material3 UI 컴포넌트 전체 (Scaffold, TopAppBar, NavigationBar 등)
import androidx.compose.runtime.Composable               // Composable 함수 선언용
import androidx.compose.ui.Modifier                      // Modifier를 통한 UI 속성 조정
import com.example.mytaplayoutapp.model.TravelMatePost  // 게시물 데이터 모델 import

/**
 * ✅ 홈 화면 전체 UI를 구성하는 Composable 함수
 * - 상단 TopAppBar: 앱 제목 + 팝업/필터 아이콘
 * - 중앙 LazyColumn: 스크롤 가능한 모집글 카드 리스트
 * - 하단 NavigationBar: 탭 내비게이션 (홈/리뷰/내 정보)
 *
 * @param posts 모집글 리스트 (TravelMatePost 데이터)
 */
@Composable
fun HomeScreen(posts: List<TravelMatePost>) {
    Scaffold(
        // 상단 앱바 (헤더 영역)
        topBar = {
            TopAppBar(
                title = { Text("Daejeon Travel Mate") }, // 앱 제목
                navigationIcon = {
                    IconButton(onClick = { /* TODO: 팝업 알림 기능 */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "팝업 아이콘")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: 필터 기능 */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "필터 아이콘")
                    }
                }
            )
        },
        // 하단 네비게이션 바
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* TODO: 홈 이동 */ },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("홈") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO: 리뷰 이동 */ },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("리뷰") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO: 내 정보 이동 */ },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    label = { Text("내 정보") }
                )
            }
        }
    ) { paddingValues ->
        // 중앙 영역: 모집글 카드 리스트 (스크롤 가능)
        LazyColumn(
            contentPadding = paddingValues, // 상하단 패딩 고려
            modifier = Modifier.fillMaxSize() // 전체 화면 채우기
        ) {
            items(posts) { post ->
                TravelMatePostCard(post) // 각 카드 표시
            }
        }
    }
}
