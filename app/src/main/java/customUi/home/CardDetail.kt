package customUi.home

import android.graphics.Outline
import android.util.Log
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.request.ImageRequest
import com.example.daejeonpass.model.TravelMatePost
import com.example.daejeonpass.model.UserProfile
import com.example.DaejeonPass.R
import com.example.daejeonpass.customUi.profile.ProfileScreen
import com.example.daejeonpass.data.PostRepository
import com.example.daejeonpass.model.UserViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.lang.String.format
import com.example.daejeonpass.model.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetail(
    post:TravelMatePost,
    onBookmarkClick: () -> Unit,
    onJoinClick: () -> Unit,
    navController: NavController,
    user: UserProfile,
    reservationViewModel: ReservationViewModel = viewModel(),
    ){
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Log.d("CardDetail", "작성자 URI: ${post.profileImage}")
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("DAEJEON Travel Mate") }, // 앱 제목
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "뒤로 가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xCBEDEEFF)
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        onBookmarkClick()
                        coroutineScope.launch {
                            try {
                                withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                    snackbarHostState.showSnackbar(
                                        message = if (!post.isBookmarked) "즐겨찾기에 추가되었습니다." else "즐겨찾기에서 제거되었습니다.",
                                        duration = SnackbarDuration.Indefinite
                                    )
                                }
                            } catch (e: TimeoutCancellationException) {
                                // 1초 지나면 자동 취소
                            }
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .shadow(4.dp, RectangleShape),
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.LightGray
                    ),
                    border = null
                ) {
                    Text(
                        if (post.isBookmarked) "즐겨찾기 해제" else "즐겨찾기",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = {
                        // 1. 작성자는 참여 불가
                        if (post.authorName == user.name) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = "작성자는 참여하실 수 없습니다", duration = SnackbarDuration.Short)
                            }
                        }
                        // 2. 이미 참여한 경우
                        else if (post.participants.any { it.name == user.name }) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = "이미 Join하셨습니다", duration = SnackbarDuration.Short)
                            }
                        }
                        // 3. 인원 초과
                        else if (post.currentpeople >= post.totalpeople) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = "인원이 다 찼습니다", duration = SnackbarDuration.Short)
                            }
                        }
                        // 4. 참여 가능
                        else {
                            // UserProfile 객체 생성 (CardDetail의 매개변수 user 사용)
                            val newParticipant = user // 현재 로그인한 사용자 정보

                            // ReservationViewModel addReservation 함수 호출
                            reservationViewModel.addReservation(
                                postId = post.id,
                                postTitle = post.title,
                                postDate = post.date,
                                // 요구사항 B에 따라 현재 post의 모든 참여자 + 새로운 참여자를 전달할 수도 있음
                                // 현재는 새로운 참여자(로그인한 유저)만 전달하는 방식
                                user = newParticipant
                            )

                            // 중요: PostRepository의 TravelMatePost 데이터도 업데이트해야 함
                            // 예시: PostRepository.addParticipantToPost(post.id, newParticipant)
                            // 이 함수는 PostRepository 내에서 post를 찾아 participants에 newParticipant를 추가하고,
                            // currentpeople을 1 증가시킨 후, posts 리스트를 업데이트해야 합니다.
                            // 실제 구현은 PostRepository의 구조에 따라 달라집니다.
                            // 임시로 Toast 메시지로 대체, 실제로는 Repository 업데이트 필요
                            PostRepository.addParticipantToPost(post.id, newParticipant) // 이 함수 구현 필요!

                            // MainActivity에서 전달된 onJoinClick 콜백 호출
                            onJoinClick()


                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {
                                        snackbarHostState.showSnackbar(
                                            message = "Join하셨습니다",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                    // 참여 후 화면을 나갈 경우
                                    // delay(1000L) // 스낵바를 잠시 보여준 후
                                    // navController.popBackStack()
                                } catch (e: TimeoutCancellationException) {
                                    // 타임아웃
                                }
                            }
                            // onJoinClick() // 기존 콜백이 필요하다면 호출
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .shadow(4.dp, RectangleShape),
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.LightGray
                    ),
                    border = null
                ) { Text("JOIN", fontWeight = FontWeight.SemiBold) }

            }
        }
    ){
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(post.profileImage),
                    contentDescription = "작성자 프로필",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text("${post.age}세 / ${post.gender}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("제목 |  ${post.title}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text("일자 |  ${post.date}", style = MaterialTheme.typography.bodyMedium)
            Text("장소 |  ${post.location}", style = MaterialTheme.typography.bodyMedium)

            if((post.totalpeople-post.currentpeople)==0){
                Text(
                    text = "인원 |  ${post.currentpeople} / ${post.totalpeople}명",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
            else{
                Text(
                    text = "인원 |  ${post.currentpeople} / ${post.totalpeople}명",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text("가격 |  ${formatPrice(post.price)}원", style = MaterialTheme.typography.bodyMedium)
            Text("기타 |  ${post.gita}", style = MaterialTheme.typography.bodyMedium)

            Text(
                text = "참여자",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )

            // 참여자 목록 UI는 PostRepository가 업데이트된 후 자동으로 Recomposition 되어야 함
            // 또는 ViewModel을 통해 Post의 상태를 관찰하고 UI를 업데이트해야 함
            val updatedPost = PostRepository.posts.find { it.id == post.id } ?: post // 업데이트된 post 정보 가져오기

            if (updatedPost.participants.isEmpty()) {
                Text("아직 참여자가 없습니다.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    updatedPost.participants.forEach { participant -> // 업데이트된 참여자 목록 사용
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(participant.profileImage ?: R.drawable.basic_profile), // null 처리
                                contentDescription = "${participant.name}의 프로필 이미지",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${participant.name} · ${participant.age}세 · ${participant.gender}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
fun formatPrice(price: Int): String {
    return "%,d".format(price)
}