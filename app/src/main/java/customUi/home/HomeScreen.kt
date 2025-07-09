package customUi.home

import android.content.Intent
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.daejeonpass.LoginActivity
import com.example.daejeonpass.model.TravelMatePost
import com.example.daejeonpass.customUi.TabItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    posts: List<TravelMatePost>,                  // 전체 게시물 리스트 (상위에서 주입)
    onWriteClick: () -> Unit,                     // 글쓰기 버튼 클릭 시 동작 (예: 글쓰기 화면 이동)
    onPostClick: (TravelMatePost) -> Unit // 게시물 카드 클릭 시 동작 (예: 상세화면 이동)
) {
    val context = LocalContext.current


    var selectedSort by remember { mutableStateOf("최신순") } // 현재 선택된 정렬 기준
    var showBookmarksOnly by remember { mutableStateOf(false) } // 즐겨찾기 필터 여부
    var showLogoutDialog by remember { mutableStateOf(false) }

    val sortOptions = listOf("금액 높은순", "금액 낮은순", "인원 적은순", "인원 많은순", "최신순") //정렬 옵션 목록
    val filteredPosts = posts
        .let { if (showBookmarksOnly) it.filter { it.isBookmarked } else it } //북마크 되어있는 것 우선 필터링
        .let {
            when (selectedSort) {
                "금액 높은순" -> it.sortedByDescending { post -> post.price }
                "금액 낮은순" -> it.sortedBy { post -> post.price }
                "인원 적은순" -> it.sortedBy { post -> post.totalpeople }
                "인원 많은순" -> it.sortedByDescending { post -> post.totalpeople }
                "최신순" -> it.sortedBy { post -> post.date }
                else -> it // 기본은 최신순
            }
        }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                title = { Text("DAEJEON Travel Mate") }, // 앱 제목
                navigationIcon = {
                    IconButton(onClick = {
                        //로그아웃 처리, LoginActivity로 돌아가면서 기존 Mainactivity의 스택 제거
                        showLogoutDialog = true
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "팝업 아이콘")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xCBEDEEFF)
                )
            )
        }
    ) { innerPadding ->
        Column( //게시물 리스트
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) //게시물 카드 간 간격
        ) {
            Row(
                modifier=Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                SortDropdown(selectedSort, sortOptions) { //정렬 드롭다운 버튼
                    selectedSort = it
                }
                Spacer(modifier = Modifier.width(8.dp)) // 정렬과 즐겨찾기 버튼 간격조정
                IconButton(onClick = { showBookmarksOnly = !showBookmarksOnly }) { //즐겨찾기 버튼
                    Icon(
                        imageVector = if (showBookmarksOnly) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "즐겨찾기 필터"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                ){
                    IconButton(onClick = onWriteClick) { //즐겨찾기 버튼
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "글쓰기"
                        )
                    }
                }


            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPosts) { post ->
                    TravelMatePostCard(
                        post = post,
                        onClick = { onPostClick(post) }
                    )
                }
            }
        }
    }
    if(showLogoutDialog){
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("로그아웃") },
            text = { Text("로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = { //예 누르고 로그아웃 실행
                    showLogoutDialog = false
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun SortDropdown(
    selected: String,
    options: List<String>,
    onSelectedChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}