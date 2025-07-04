package com.example.mytaplayoutapp.customUi.home

// ✅ 필요한 Compose UI 컴포넌트 및 라이브러리 import
import androidx.compose.foundation.Image              // 이미지 표시용 Composable
import androidx.compose.foundation.layout.*           // 레이아웃 관련 Modifier (Row, Column, Spacer 등)
import androidx.compose.material3.Card                // 카드 UI 컴포넌트
import androidx.compose.material3.CardDefaults        // 카드 스타일 설정 (elevation 등)
import androidx.compose.material3.MaterialTheme       // Material3 테마 스타일
import androidx.compose.material3.Text                // 텍스트 표시용 Composable
import androidx.compose.runtime.Composable            // Composable 함수 선언용
import androidx.compose.ui.Alignment                  // 레이아웃 정렬 설정용
import androidx.compose.ui.Modifier                   // Modifier를 통한 UI 속성 조정
import androidx.compose.ui.res.painterResource        // 이미지 리소스 불러오기용
import androidx.compose.ui.unit.dp                    // dp 단위 설정용
import com.example.mytaplayoutapp.model.TravelMatePost // 게시물 데이터 모델 import

/**
 * ✅ 모집글 카드 UI를 표시하는 Composable 함수
 * - 각 모집글 정보를 카드 형태로 깔끔하게 보여줌
 *
 * @param post 표시할 모집글 데이터 (TravelMatePost)
 */
@Composable
fun TravelMatePostCard(post: TravelMatePost) {
    Card(
        modifier = Modifier
            .padding(8.dp)          // 카드 외부 여백
            .fillMaxWidth(),        // 카드 폭을 전체 너비로 설정
        elevation = CardDefaults.cardElevation(4.dp) // 카드 그림자 깊이 설정
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // 카드 내부 여백 설정
            // 상단 Row: 프로필 이미지 + 제목 나란히 배치
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = post.profileImage), // 프로필 이미지
                    contentDescription = "프로필 이미지",            // 접근성 설명
                    modifier = Modifier.size(48.dp)                  // 이미지 크기 설정
                )
                Spacer(modifier = Modifier.width(8.dp)) // 이미지와 텍스트 사이 간격
                Text(
                    text = post.title,                                   // 모집글 제목
                    style = MaterialTheme.typography.titleMedium        // 테마의 중간 제목 스타일 적용
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // 하단 텍스트 정보 표시 (날짜, 장소, 금액, 인원)
            Text("일자: ${post.date}")
            Text("장소: ${post.location}")
            Text("금액: ${post.price}")
            Text("인원: ${post.people}")
        }
    }
}
