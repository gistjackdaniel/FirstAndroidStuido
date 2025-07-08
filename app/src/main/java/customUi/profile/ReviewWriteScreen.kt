package customUi.profile // 또는 적절한 패키지

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.DaejeonPass.R
import com.example.daejeonpass.data.ReviewDetails
import com.example.daejeonpass.model.ReviewViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

// 고유한 reviewId 생성을 위한 AtomicInteger (ViewModel로 옮기는 것이 더 적절할 수 있음)
// 실제 앱에서는 서버에서 ID를 받거나 UUID 등을 사용합니다.
private val nextReviewId = AtomicInteger(21) // 기존 더미 데이터와 겹치지 않도록 초기값 설정

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewWriteScreen(
    navController: NavController,
    viewModel: ReviewViewModel,
    pastTripTitle: String?, // 어떤 여행에 대한 후기인지 제목을 받아올 수 있음 (선택적)
    // passedImageUri: Uri? // ProfileScreen에서 이미지를 먼저 선택했다면 전달받을 수 있음
) {
    var title by remember { mutableStateOf(pastTripTitle?.let { "$it 후기" } ?: "") }
    var content by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0f) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // 선택된 이미지 URI



    // 이미지 선택을 위한 ActivityResultLauncher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }



    val context = LocalContext.current
    val currentAuthorName = "나" // TODO: 실제 사용자 이름으로 대체
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("후기 작성") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank() && selectedImageUri != null) {
                                val newReviewId = nextReviewId.getAndIncrement()
                                // TODO: selectedImageUri를 ViewModel을 통해 실제 저장 가능한 형태로 변환/업로드 필요
                                // 지금은 임시로 R.drawable 리소스를 사용한다고 가정하고, 실제로는 URI를 다뤄야 함
                                // ViewModel에서 Uri를 받아서 내부 저장소에 복사하고 그 경로를 저장하거나,
                                // 서버에 업로드하고 URL을 받는 등의 처리가 필요합니다.
                                // 여기서는 UI 데모를 위해 임의의 drawable을 사용합니다.
                                // val tempImageResForDemo = "android.resource://com.example.daejeonpass/${R.drawable.sample1}" // 실제로는 selectedImageUri 기반이어야 함

                                // selectedImageUri를 String으로 변환하여 ReviewDetails에 저장
                                // ViewModel에서 이 URI 문자열을 사용하여 실제 이미지 파일을 내부 저장소에 복사하거나
                                // 서버에 업로드하고 URL을 받는 등의 처리가 필요합니다.
                                val imageUriString = selectedImageUri.toString()



                                val newReviewDetails = ReviewDetails(
                                    reviewId = newReviewId,
                                    title = title,
                                    content = content,
                                    authorName = currentAuthorName,
                                    profileImageUri = R.drawable.basic_profile, // TODO: 현재 사용자 프로필
                                    reviewImageRes = imageUriString, // << 변경: 선택된 이미지 URI 문자열 사용
                                    // reviewImageRes = tempImageResForDemo, // << 중요: 실제로는 selectedImageUri 처리 필요
                                    date = currentDate,
                                    rating = rating
                                )
                                viewModel.addOrUpdateReview(newReviewDetails)
                                navController.popBackStack()
                            } else {
                                // 유효성 검사 실패 메시지
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank() && selectedImageUri != null
                    ) {
                        Text("저장")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 대표 이미지 선택 ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        imagePickerLauncher.launch("image/*") // 이미지 선택기 실행
                        // 클릭 시 테스트용 Drawable 리소스를 번갈아 선택하거나, 특정 리소스로 고정
                        // 예시: R.drawable.profile1 또는 다른 샘플 이미지 리소스 ID
                        // selectedImageResId = if (selectedImageResId == R.drawable.profile1) {
                        //    R.drawable.sample1 // 다른 테스트 이미지로 변경 가능
                        //} else {
                        //    R.drawable.profile1
                        // }
                        //imagePickerLauncher.launch("image/*") // 이미지 선택기 실행
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        // Coil 라이브러리를 사용하여 Uri 로드 (build.gradle에 Coil 의존성 추가 필요)
                        // implementation("io.coil-kt:coil-compose:2.6.0")
                        painter = rememberAsyncImagePainter(model = selectedImageUri),
                        contentDescription = "선택된 대표 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "사진 추가",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("사진을 선택하세요", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- 제목 입력 ---
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- 별점 선택 ---
            Text("별점", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                (1..5).forEach { starIndex ->
                    Icon(
                        imageVector = if (starIndex <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "별점 $starIndex",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { rating = starIndex.toFloat() },
                        tint = if (starIndex <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- 내용 입력 ---
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("여행 후기를 작성해주세요.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- 작성자 정보 (자동으로 표시) ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.basic_profile), // TODO: 현재 사용자 프로필
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(currentAuthorName, fontWeight = FontWeight.Bold)
                    Text(currentDate, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}