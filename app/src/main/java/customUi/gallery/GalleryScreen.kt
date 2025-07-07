package com.example.daejeonpass.customUi.gallery // 패키지 경로 설정 (앱의 위치 정의)

// ✅ 필요한 Compose UI 컴포넌트 및 라이브러리 import
import androidx.compose.foundation.Image // 이미지 표시를 위한 컴포넌트
import androidx.compose.foundation.layout.* // 레이아웃 관련 컴포넌트 (Row, Column 등)
import androidx.compose.foundation.lazy.grid.GridCells // 그리드 열 설정용
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid // 스크롤 가능한 그리드 리스트
import androidx.compose.foundation.lazy.grid.items // 그리드 아이템 생성용
import androidx.compose.material.icons.Icons // 기본 제공 아이콘 세트
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.filled.Add // 추가(+) 아이콘 (리뷰 작성용)
import androidx.compose.material.icons.filled.Notifications // 알림 아이콘
import androidx.compose.material3.* // Material 3 디자인 시스템 컴포넌트
import androidx.compose.runtime.Composable // Composable 함수 선언용
import androidx.compose.ui.Modifier // Modifier로 UI 속성 제어
import androidx.compose.ui.res.painterResource // 리소스 이미지 불러오기
import androidx.compose.ui.unit.dp // dp 단위 설정용
import androidx.compose.ui.layout.ContentScale //  ContentScale import 추가
import androidx.compose.foundation.clickable //  클릭 동작 추가를 위한 import
import androidx.navigation.NavController
import com.example.daejeonpass.R

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
// If you are using delegated properties (e.g., var text by remember { mutableStateOf("") })
// you might also need these:
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.example.daejeonpass.data.ReviewComment
import com.example.daejeonpass.data.ReviewThumbnailInfo
import com.example.daejeonpass.model.CommentViewModel
import com.example.daejeonpass.model.ReviewDetails // 새로 추가한 데이터 클래스 임포트
import kotlin.text.isNotBlank
import androidx.compose.runtime.snapshots.SnapshotStateList // 추가

/**
 * ✅ GalleryScreen (리뷰 화면) Composable 함수
 * - 상단: 앱 로고, 알림 아이콘, 리뷰 작성 아이콘
 * - 중앙: 썸네일 그리드 리스트 (최소 20개, 스크롤 가능)
 * - 각 썸네일 클릭 시 리뷰 상세 화면으로 이동
 */
@OptIn(ExperimentalMaterial3Api::class) // Experimental API 사용 시 필수 (경고 제거)
@Composable // Composable 함수 선언 (UI 함수)
fun GalleryScreen(navController: NavController) {

    val thumbnailResources = listOf(
        R.drawable.sample1, R.drawable.sample2, R.drawable.sample3, R.drawable.sample4,
        R.drawable.sample5, R.drawable.sample6, R.drawable.sample7, R.drawable.sample8,
        R.drawable.sample9, R.drawable.sample10, R.drawable.sample11, R.drawable.sample12,
        R.drawable.sample13, R.drawable.sample14, R.drawable.sample15, R.drawable.sample17,
        R.drawable.sample18, R.drawable.sample19, R.drawable.sample20
    )

    val reviewThumbnailInfos: List<ReviewThumbnailInfo> = thumbnailResources.mapIndexed { index, imageRes ->
        ReviewThumbnailInfo(reviewId = index + 1, imageRes = imageRes) // index + 1을 reviewId로 사용
    }


    Scaffold(
        // Scaffold: 상단바/하단바/본문 구조를 쉽게 구성하는 레이아웃
        topBar = { // 상단 앱바 영역
            TopAppBar(
                title = { Text("DAEJEON Travel Mate") }, // 앱 제목 표시
                navigationIcon = {
                    IconButton(onClick = { /* TODO: 알림 화면 이동 */ }) { // 알림 버튼
                        Icon(Icons.Default.Notifications, contentDescription = "알림") // 알림 아이콘
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: 리뷰 작성 화면 이동 */ }) { // 리뷰 작성 버튼
                        Icon(Icons.Default.Add, contentDescription = "리뷰 작성") // 추가 아이콘
                    }
                }
            )
        },
    ) { paddingValues -> // Scaffold의 본문 영역 (paddingValues는 상단바 높이 반영용)
        LazyVerticalGrid( // 스크롤 가능한 그리드 레이아웃
            columns = GridCells.Fixed(3), // 3열 고정 그리드
            contentPadding = paddingValues, // 상단/하단 패딩 적용
            modifier = Modifier.fillMaxSize() // 화면 전체 크기 채움
        ) {
            items(reviewThumbnailInfos, key = { it.reviewId }) { thumbnailInfo -> // 썸네일 리스트 아이템 순회
                Image( // 이미지 표시.
                            painter = painterResource(id = thumbnailInfo.imageRes), // 리소스 ID로 이미지 로드
                            contentDescription = "리뷰 썸네일", // 접근성 설명
                            modifier = Modifier
                                .aspectRatio(1f) // 정사각형 비율 유지
                                .padding(4.dp) // 이미지 간 여백 설정
                                .clickable {
                                    //  thumbnailInfo에서 reviewId와 imageRes를 가져와 전달하고, 썸네일 클릭 시 리뷰 상세 화면으로 이동
                                    navController.navigate("review_detail/${thumbnailInfo.imageRes}/${thumbnailInfo.reviewId}")
                                },
                            // {onThumbnailClick(imageRes)} , //  클릭 시 이미지 ID 전달
                    contentScale = ContentScale.Crop //  가로/세로 사진 상관없이 꽉 채움 (중앙 잘림 가능)
                )
            }
        }
    }
}



/**
 * ✅ ReviewDetailScreen (리뷰 상세 화면) Composable 함수
 * - 상단: 앱 로고, 알림 아이콘
 * - 중앙: 리뷰 제목, 대표 이미지, 리뷰 내용, 댓글 리스트, 댓글 작성 영역
 * - 하단 네비게이션 바는 TabItem으로 따로 구현됨 (여기선 생략)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailScreen(
    navController: NavController,
    reviewId: Int,          // 네비게이션으로 전달받는 리뷰 ID
    imageResFromNav: Int, // 네비게이션으로 전달받는 대표 이미지 (ViewModel의 loadReviewData에 사용)
    viewModel: CommentViewModel // ViewModel 인스턴스
) {
    LaunchedEffect(key1 = reviewId, key2 = imageResFromNav) {
        viewModel.loadReviewData(reviewId, imageResFromNav)
    }


    // ViewModel로부터 StateFlow를 구독하여 리뷰 상세 정보와 ReviewComment 리스트를 가져옵니다.
    val reviewDetailsData by viewModel.reviewDetails.collectAsState()
    // --- 댓글 상태 가져오는 방식 변경 ---
    // 이전: val commentsState by viewModel.getCommentsFlow(reviewId).collectAsState()
    // 변경: SnapshotStateList를 직접 사용
    val commentsState: SnapshotStateList<ReviewComment> = viewModel.getCommentsForReview(reviewId)
    // --- 댓글 상태 가져오는 방식 변경 완료 ---


    // reviewDetailsData가 null (로딩 중)일 때와 아닐 때를 구분하여 UI 표시
    reviewDetailsData?.let { details -> // details는 ReviewDetails 타입
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("DAEJEON Travel Mate") }, // Or whatever title is appropriate
                    navigationIcon = { // For the icon on the far left
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로 가기"
                                // Optional: tint = MaterialTheme.colorScheme.onPrimary // If needed for visibility
                            )
                        }
                    },
                    actions = { // For icons on the right side
                        IconButton(onClick = { /* TODO: 알림 화면 이동 */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "알림"
                                // Optional: tint = MaterialTheme.colorScheme.onPrimary // If needed for visibility
                            )
                        }
                        // You can add more IconButton actions here if needed
                        // For example:
                        // IconButton(onClick = { /* Another action */ }) {
                        //     Icon(Icons.Default.Share, contentDescription = "공유")
                        // }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // --- 리뷰 상세 정보 표시 ---
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = details.profileImageRes),// ViewModel 데이터 사용
                            contentDescription = "프로필 이미지",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = details.authorName,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = details.date,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = details.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            val imageVector =
                                if (index < details.rating.toInt()) Icons.Filled.Star else Icons.Outlined.StarOutline
                            Icon( // Use Icon composable
                                imageVector = imageVector,
                                contentDescription = "별점",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = details.reviewImageRes),
                        contentDescription = "대표 이미지",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = details.content, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "댓글 (${commentsState.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // --- 댓글 목록 표시 ---
                if (commentsState.isEmpty()) {
                    item {
                        Text(
                            "아직 댓글이 없습니다. 첫 댓글을 남겨주세요!",
                            modifier = Modifier.padding(vertical = 16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(items = commentsState, key = { it.id }) { reviewComment ->
                        CommentListItemFromData(comment = reviewComment)
                    }
                }

                // 댓글 입력 UI
                item {
                    CommentInputSection( // 기존 CommentInputSection 재활용 가능
                        onUpload = { newCommentText ->
                            // 실제 앱에서는 현재 로그인한 사용자 이름을 가져와야 합니다.
                            viewModel.addCommentToReview(reviewId, "현재 사용자", newCommentText)
                        }
                    )
                }
            }
        }
    } ?: run {
    // reviewDetailsData가 null일 경우 (로딩 중) 표시할 UI
    Scaffold( // Scaffold를 사용하여 TopAppBar 일관성 유지
        topBar = {
            TopAppBar(
                title = { Text("DAEJEON Travel Mate") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                actions = { /* 로딩 중에는 액션 버튼 비활성화 또는 숨김 처리 가능 */ }
            )
        }
    ){ paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Text("리뷰를 불러오는 중...", modifier = Modifier.padding(top = 60.dp))
        }
    }
}
}

/**
 * 📍 댓글 리스트 아이템 Composable 함수
 */
@Composable
fun CommentListItemFromData(comment: ReviewComment) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 프로필 이미지는 comment.profileImageUrl 등이 있다면 사용
            Image(
                painter = painterResource(id = R.drawable.profile1), // 예시 프로필
                contentDescription = "프로필 이미지",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = comment.authorName, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.weight(1f))
            // timestamp를 사용하여 "5분 전", "2023-10-27" 등으로 변환하여 표시 가능
            Text(
                text = android.text.format.DateUtils.getRelativeTimeSpanString(comment.timestamp).toString(),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = comment.content,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 44.dp)
        )
    }
}


/**
 * 📍 댓글 입력창 Composable 함수
 * - 댓글 입력 텍스트필드 + 업로드 버튼 포함
 * - 댓글 입력 시 버튼 활성화
 * @param onUpload 댓글 등록 콜백 함수
 */
@Composable
fun CommentInputSection(onUpload: (String) -> Unit) {
    var comments by remember { mutableStateOf("") } // 입력 상태 기억

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)) {
        OutlinedTextField(
            value = comments,
            onValueChange = { comments = it },
            label = { Text("댓글을 입력하세요") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = {
                    if (comments.isNotBlank()) {
                        onUpload(comments)
                        comments = "" // 입력창 비우기
                    }
                },
                enabled = comments.isNotBlank() // 댓글이 비어있지 않을 때만 활성화
            ) {
                Text("Upload")
            }
        }
    }
}



