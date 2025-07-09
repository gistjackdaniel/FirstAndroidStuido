/*
목표: 지난 동행 내역에서 작성한 후기가 두 번째 갤러리 탭에 등록되도록 Compose UI를 설계한다.
- 리뷰 작성: Profile 탭 (TabItem.Profile)
- 리뷰 등록: Gallery 탭 (TabItem.Gallery)
- 리뷰 ID 및 이미지 정보는 ReviewThumbnailInfo.kt를 활용
- 댓글 데이터는 reviewComment.kt 구조 기반
*/

// ProfileScreen.kt (지난 동행 내역 + 후기 작성 화면)

// 파일 경로: app/src/main/java/com/example/mytablayoutapp/customUi/profile/ProfileScreen.kt
package com.example.daejeonpass.customUi.profile



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.DaejeonPass.R
import com.example.daejeonpass.data.ReviewThumbnailInfo
import com.example.daejeonpass.data.ReviewDetails
import com.example.daejeonpass.model.ReviewViewModel

/**
 * 프로필 탭에 표시될 화면입니다.
 */
@Composable
fun ProfileScreen(
    navController: NavController, // NavController 주입
    viewModel: ReviewViewModel,
    onNavigateToGallery: () -> Unit // 이 부분은 이제 ReviewWriteScreen에서 처리 후 돌아오므로 직접 사용 안 할 수도 있음
) {
    val reservationList = remember { sampleReservationList() }
    val pastTrips = remember { samplePastTripList() }

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // 1. 예약된 동행 내역 섹션
        item {
            Text("예약된 동행 내역", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
        }
        if (reservationList.isEmpty()) {
            item {
                Text(
                    "예약된 동행 내역이 없습니다.",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(reservationList) { reservation -> // reservationList의 각 아이템에 대해
                ReservationCard(trip = reservation)    // ReservationCard를 표시
            }
        }

        // 2. 지난 동행 내역 섹션 (+ 후기 작성)
        item {
            Spacer(Modifier.height(16.dp))
            Text("지난 동행 내역", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
        }
        if (pastTrips.isEmpty()) {
            item {
                Text(
                    "지난 동행 내역이 없습니다.",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(pastTrips) { trip ->
                PastTripCard(trip = trip, onWriteReview = { pastTrip ->
                    // PastTrip의 title을 URL 인코딩하여 전달하는 것이 안전할 수 있음
                    // 여기서는 간단히 문자열 그대로 전달
                    val route = "review_write_screen?pastTripTitle=${pastTrip.title}"
                    navController.navigate(route)
                })
            }
        }
    }
}

/**
 * 지난 동행 내역을 표시하고 후기 작성 버튼을 포함하는 카드 Composable
 */
@Composable
fun PastTripCard(trip: PastTrip, onWriteReview: (PastTrip) -> Unit) { // trip 객체 전체를 전달하도록 변경
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(trip.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("날짜: ${trip.date}", style = MaterialTheme.typography.bodyMedium)
            Text("동행자: ${trip.partnerName}", style = MaterialTheme.typography.bodyMedium)
            // trip.imageRes를 사용하여 작은 썸네일 이미지를 여기에 표시할 수도 있습니다.
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onWriteReview(trip) }, // trip 정보를 콜백으로 전달
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("후기 작성하기")
            }
        }
    }
}

@Composable
fun ReservationCard(trip: ReservationTrip) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(trip.title, style = MaterialTheme.typography.titleMedium)
            Text("예약일: ${trip.date}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("참여자:", style = MaterialTheme.typography.titleSmall)
            if (trip.participants.isEmpty()) {
                Text("참여자가 없습니다.", style = MaterialTheme.typography.bodySmall)
            } else {
                // UserProfile 객체를 순회
                trip.participants.forEach { participant -> // participant는 이제 UserProfile 타입
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Image(
                            painter = if (participant.profileImage != null) { // UserProfile의 profileImage 사용
                                rememberAsyncImagePainter(model = participant.profileImage)
                            } else {
                                painterResource(id = R.drawable.profile_placeholder) // 기본 프로필 이미지
                            },
                            contentDescription = "${participant.name} 프로필 이미지", // UserProfile의 name 사용
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(participant.name, style = MaterialTheme.typography.bodyMedium) // UserProfile의 name 사용
                    }
                }
            }
        }
    }
}

/**
 * 지난 동행 내역을 표시하고 후기 작성 버튼을 포함하는 카드 Composable
 */
@Composable
fun PastTripCard(trip: PastTrip, onWriteReview: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(trip.title, style = MaterialTheme.typography.titleMedium) // 글씨 크기 약간 조정
            Spacer(Modifier.height(4.dp))
            Text("날짜: ${trip.date}", style = MaterialTheme.typography.bodyMedium)
            Text("동행자: ${trip.partnerName}", style = MaterialTheme.typography.bodyMedium)
            // TODO: trip.imageRes를 사용하여 작은 썸네일 이미지를 여기에 표시할 수도 있습니다.
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onWriteReview, // 버튼 클릭 시 전달받은 람다 실행
                modifier = Modifier.align(Alignment.End) // 버튼을 카드 오른쪽으로 정렬 (선택적)
            ) {
                Text("후기 작성하기")
            }
        }
    }
}


// --- 샘플 데이터 정의 ---
// 실제 앱에서는 이 데이터들을 ViewModel, Repository 또는 API로부터 가져옵니다.

data class ReservationTrip(
    val id: Int,
    val title: String,
    val date: String,
    val participants: List<UserProfile>
)

data class PastTrip(
    val id: Int,
    val title: String,
    val date: String,
    val partnerName: String,
    val imageRes: Int // 후기 작성 시 사용할 이미지 리소스 ID
)

fun sampleReservationList() = listOf(
    ReservationTrip(101, "Visit Daejeon Seongsimdang", "2025-08-15", "Jack, Emily"),
    ReservationTrip(102, "유성온천 족욕체험 예약", "2025-08-01", "김철수, 김영희")
)

fun samplePastTripList() = listOf(
    PastTrip(201, "대전 스카이로드 야경", "2024-07-10", "이영희", R.drawable.sample3), // 여러분의 drawable 리소스로 변경
    PastTrip(202, "장태산 자연휴양림 힐링", "2024-06-20", "박지민", R.drawable.sample4)  // 여러분의 drawable 리소스로 변경
)