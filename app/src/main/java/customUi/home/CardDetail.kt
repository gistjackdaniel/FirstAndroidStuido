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
import androidx.navigation.NavController
import coil.request.ImageRequest
import com.example.daejeonpass.model.TravelMatePost
import com.example.daejeonpass.model.UserProfile
import com.example.DaejeonPass.R
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.lang.String.format

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetail(
    post:TravelMatePost,
    onBookmarkClick: () -> Unit,
    onJoinClick: () -> Unit,
    navController: NavController,
    user: UserProfile
){
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Log.d("CardDetail", "작성자 URI: ${post.profileImage}")
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Daejeon Travel Mate") }, // 앱 제목
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "뒤로 가기")
                    }
                }
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
                    onClick = { //작성자는 post, 유저는 user에 저장
                        if (post.authorName==user.name) {
                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                        snackbarHostState.showSnackbar(
                                            message = "작성자는 참여하실 수 없습니다",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    // 1초 지나면 자동 취소
                                }
                            }
                        }
                        else if ((post.participants.any { it.name == user.name })) {
                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                        snackbarHostState.showSnackbar(
                                            message = "이미 Join하셨습니다",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    // 1초 지나면 자동 취소
                                }
                            }
                        }
                        else if (post.currentpeople >= post.totalpeople) {
                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                        snackbarHostState.showSnackbar(
                                            message = "인원이 다 찼습니다",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    // 1초 지나면 자동 취소
                                }
                            }
                        } else {
                            onJoinClick()
                            coroutineScope.launch {
                                try {
                                    withTimeout(1000L) {  // 1초 동안만 showSnackbar 유지
                                        snackbarHostState.showSnackbar(
                                            message = "Join하셨습니다",
                                            duration = SnackbarDuration.Indefinite
                                        )
                                    }
                                } catch (e: TimeoutCancellationException) {
                                    // 1초 지나면 자동 취소
                                }
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
                    style = MaterialTheme.typography.titleMedium
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

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)){
                post.participants.forEach { participant ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(participant.profileImage),
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
fun formatPrice(price: Int): String {
    return "%,d".format(price)
}