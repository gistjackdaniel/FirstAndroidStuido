// 파일 경로: app/src/main/java/com/example/daejeonpass/MainActivity.kt
package com.example.daejeonpass // 여러분의 프로젝트 패키지 이름

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle // 액티비티 상태 저장을 위한 번들
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity // Compose를 사용하는 액티비티의 기본 클래스
import androidx.activity.compose.setContent // 액티비티의 컨텐츠를 Compose UI로 설정하는 함수
import androidx.activity.enableEdgeToEdge // 전체 화면 사용(엣지 투 엣지)을 활성화 (선택 사항)
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding // UI 요소에 여백을 주기 위함
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.* // Material 3 컴포넌트 전체 import
import androidx.compose.runtime.* // remember, mutableStateOf 등 Compose의 상태 관리 함수
import androidx.compose.ui.Modifier // UI 요소 수정을 위한 Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination.Companion.hierarchy // 내비게이션 계층 구조를 다루기 위함
import androidx.navigation.NavGraph.Companion.findStartDestination // 내비게이션 그래프의 시작점 찾기
import androidx.navigation.NavHostController // 내비게이션을 제어하는 컨트롤러
import androidx.navigation.NavType
import androidx.navigation.compose.* // rememberNavController, NavHost, composable 등 Compose Navigation 관련 함수
import androidx.navigation.navArgument

// 3단계에서 만든 화면 Composable 함수들을 import 합니다.
import customUi.home.HomeScreen
import com.example.daejeonpass.customUi.gallery.GalleryScreen
import com.example.daejeonpass.model.TravelMatePost
import com.example.daejeonpass.model.UserViewModel

// 4단계에서 만든 TabItem sealed class를 import 합니다.
import com.example.daejeonpass.customUi.TabItem
import com.example.daejeonpass.data.PostRepository
import com.example.daejeonpass.model.UserProfile
// 프로젝트의 테마를 import 합니다. (프로젝트 생성 시 자동 생성됨)
import com.example.daejeonpass.ui.theme.KoreaPassTheme // 여러분의 프로젝트 테마 이름으로 변경
import com.example.DaejeonPass.R
import com.google.gson.Gson
import customUi.home.CardDetail
import customUi.home.NewPost
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daejeonpass.customUi.gallery.ReviewDetailScreen
import com.example.daejeonpass.customUi.profile.ProfileScreen
import com.example.daejeonpass.customUi.profile.ReviewWriteScreen
import com.example.daejeonpass.model.ReviewViewModel
import com.example.daejeonpass.model.ReviewViewModelFactory
import com.example.daejeonpass.utils.drawablePngToUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.PrimitiveKind.SHORT
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        val loginSuccess = intent.getBooleanExtra("loginSuccess", false)
        if (loginSuccess){
            Toast.makeText(this, "로그인되었습니다", Toast.LENGTH_SHORT).show()
        }
        super.onCreate(savedInstanceState) // 부모 클래스의 onCreate 호출
        enableEdgeToEdge() // 화면의 시스템 바 영역까지 앱 컨텐츠를 확장 (선택 사항)
        // setContent 블록 내부에 앱의 전체 UI를 Compose로 정의합니다.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val userViewModel = (application as MyApplication).userViewModel

        setContent {

            val nickname by userViewModel.nickname.collectAsState()
            val age by userViewModel.age.collectAsState()
            val gender by userViewModel.gender.collectAsState()
            val profileUri by userViewModel.profileImageUri.collectAsState()

            // KoreaPassTheme은 앱의 전반적인 스타일(색상, 글꼴 등)을 정의합니다.
            // 프로젝트 생성 시 자동으로 만들어진 이름을 사용하세요.
            KoreaPassTheme {
                // MainScreen Composable 함수를 호출하여 앱의 메인 화면을 표시합니다.
                MainScreen(
                    nickname = nickname,
                    age = age,
                    gender = gender,
                    profileUri = profileUri
                )
            }
        }
    }
}

/**
 * 앱의 메인 화면으로, 하단 탭 네비게이션과 각 탭에 해당하는 컨텐츠를 포함합니다.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class) // 일부 Material 3 API는 실험적일 수 있음을 명시
@Composable
fun MainScreen(nickname: String, age: Int, gender: String, profileUri : Uri?) {

    // rememberNavController(): 내비게이션 상태를 기억하고 관리하는 NavController 인스턴스를 생성합니다.
    // 화면 회전 등 구성 변경에도 상태를 유지합니다.
    val navController = rememberNavController()

    val application = LocalContext.current.applicationContext as Application

    val reviewViewModel: ReviewViewModel = viewModel(
        factory = ReviewViewModelFactory(application) // Factory를 통해 'application' 매개변수 간접 전달
    )

    val tabs = listOf(
        TabItem.Home,
        TabItem.Gallery,
        TabItem.Profile
    ) // 표시할 탭들의 목록을 정의합니다. TabItem에서 정의한 객체들을 사용합니다.

    // Scaffold는 Material Design의 기본적인 화면 구조(상단 바, 하단 바, 본문 등)를 제공합니다.
    Scaffold(
        bottomBar = { // 화면 하단에 표시될 네비게이션 바
            NavigationBar { // Material 3의 하단 네비게이션 바 컴포넌트
                // 현재 선택된 탭의 라우트(경로)를 가져옵니다.
                // currentBackStackEntryAsState()는 현재 내비게이션 스택의 최상단 항목을 관찰하여
                // 화면이 변경될 때마다 selectedRoute를 업데이트합니다.
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // 정의된 탭 목록(tabs)을 순회하면서 각 탭에 대한 NavigationBarItem을 만듭니다.
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) }, // 탭 아이콘
                        label = { Text(tab.title) }, // 탭 제목
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true, // 현재 탭이 선택되었는지 여부
                        onClick = { // 탭을 클릭했을 때 실행될 동작
                            navController.navigate(tab.route) {
                                // 내비게이션 그래프의 시작점으로 popUpTo를 설정하여
                                // 백 스택에 동일한 화면이 중복으로 쌓이는 것을 방지합니다.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true // 이전 화면의 상태를 저장합니다.
                                }
                                // 이미 스택에 해당 화면이 있다면 새로 만들지 않고 기존 화면을 사용합니다.
                                launchSingleTop = true
                                // 저장된 상태가 있다면 복원합니다.
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding -> // Scaffold의 본문 영역에 적용될 패딩 값 (상단 바, 하단 바 크기 등 고려)
        // AppNavigation Composable을 호출하여 탭에 따라 다른 화면을 표시합니다.
        // innerPadding을 전달하여 내용이 시스템 바나 네비게이션 바에 가려지지 않도록 합니다.
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding), // 패딩 적용
            reviewViewModel = reviewViewModel,
            nickname, age, gender, profileUri
        )
    }
}


/**
 * NavHost를 사용하여 각 탭(라우트)에 맞는 화면(Composable)을 연결합니다.
 *
 * @param navController 내비게이션을 제어하는 컨트롤러
 * @param modifier 외부에서 전달받는 Modifier (여기서는 Scaffold의 패딩을 적용하기 위함)
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier, reviewViewModel: ReviewViewModel,
                  nickname: String, age: Int, gender: String, profileUri: Uri?) {
    val context = LocalContext.current

    if(!PostRepository.dummyDataAdded){
        var samplePosts = listOf(
            TravelMatePost(
                id = 1,
                profileImage = context.drawablePngToUri(R.drawable.boy, "boy.png"),
                authorName = "윤종범",
                gender = "남",
                age = 23,
                title = "대전 투어 같이 가요!",
                date = "2025-07-13",
                location = "대전역",
                price = 74300,
                totalpeople = 4,
                currentpeople = 1,
                gita = "야경 구경 후 야식",
                isBookmarked = false,
                participants = emptyList()
            ),
            TravelMatePost(
                id = 2,
                profileImage = context.drawablePngToUri(R.drawable.girl, "girl.png"),
                authorName = "김영희",
                gender = "여",
                age = 21,
                title = "성심당 갈 사람",
                date = "2025-07-17",
                location = "성심당",
                price = 15000,
                totalpeople = 6,
                currentpeople = 1,
                gita = "야경 구경 후 야식",
                isBookmarked = true,
                participants = emptyList()
            ),
            TravelMatePost(
                id = 3,
                profileImage = context.drawablePngToUri(R.drawable.man, "man.png"),
                authorName = "김철수",
                gender = "남",
                age = 24,
                title = "학교 축제 놀사람 구합니다",
                date = "2025-07-10",
                location = "KAIST",
                price = 324000,
                totalpeople = 4,
                currentpeople = 1,
                gita = "야경 구경 후 야식",
                isBookmarked = false,
                participants = emptyList()
            )
        ) //             --- 더미데이터
        PostRepository.createAndAddPost(samplePosts[0])
        PostRepository.createAndAddPost(samplePosts[1])
        PostRepository.createAndAddPost(samplePosts[2])

        PostRepository.dummyDataAdded = true
    }


    // NavHost는 내비게이션 그래프의 컨테이너 역할을 합니다.
    // navController를 통해 현재 표시할 화면을 결정하고, startDestination으로 앱 시작 시 첫 화면을 지정합니다.
    NavHost(
        navController = navController,
        startDestination = TabItem.Home.route, // 앱 시작 시 "연락처" 탭을 기본으로 표시
        modifier = modifier // Scaffold로부터 전달받은 패딩 등을 적용
    ) {
        // composable(route) { Composable 함수 } 형식으로 각 route와 화면을 매핑합니다.
        composable(TabItem.Home.route) { // "home_screen" 경로일 때 HomeScreen 표시
            HomeScreen(
                posts = PostRepository.posts,
                onWriteClick = { navController.navigate("new_post") },
                onPostClick = { post ->
                    navController.navigate("detail/${post.id}")
                 }
            )
        }
        composable(TabItem.Gallery.route) { // "gallery_screen" 경로일 때 GalleryScreen 표시
            GalleryScreen(
                navController = navController,
                viewModel = reviewViewModel,
            )
        }
        composable(TabItem.Profile.route) { // "profile_screen" 경로일 때 ProfileScreen 표시
            ProfileScreen(navController = navController,
                viewModel = reviewViewModel,
                onNavigateToGallery = {
                    navController.navigate(TabItem.Gallery.route) {
                        // 네비게이션 옵션 (예: 백스택 관리)
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                } )
        }
        composable("review_detail/{imageResUriString}/{reviewId}",
            arguments = listOf(
                navArgument("imageResUriString") { type = NavType.StringType },
                navArgument("reviewId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val encodedUriStringArg = backStackEntry.arguments?.getString("imageResUriString") ?: ""
            // URL 디코딩
            val imageResUriStringArg = try {
                java.net.URLDecoder.decode(encodedUriStringArg, "UTF-8")
            } catch (e: Exception) {
                Log.e("AppNavigation", "Error decoding URI: $encodedUriStringArg", e)
                "" // 디코딩 실패 시 빈 문자열 또는 기본값
            }
            val reviewIdFromNav = backStackEntry.arguments?.getInt("reviewId") ?: 0 // 기본값 또는 오류 처리

            ReviewDetailScreen(
                navController = navController,
                viewModel = reviewViewModel,
                reviewId = reviewIdFromNav,
                imageResFromNav = imageResUriStringArg, // 디코딩된 String 타입의 URI 전달
                userProfile = UserProfile(profileUri, nickname, gender, age)
            )
        }

        composable(
            route = "review_write_screen?pastTripTitle={pastTripTitle}", // 선택적 인자 pastTripTitle
            arguments = listOf(
                navArgument("pastTripTitle") {
                    type = NavType.StringType
                    nullable = true // 제목이 없을 수도 있으므로 nullable
                }
            )
        ) { backStackEntry ->
            val pastTripTitleArg = backStackEntry.arguments?.getString("pastTripTitle")
            ReviewWriteScreen(
                navController = navController,
                viewModel = reviewViewModel,
                pastTripTitle = pastTripTitleArg,
                userProfile = UserProfile(profileUri, nickname, gender, age)
            )
        }

        composable(
            route = "detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ){
            backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId")
            val post = PostRepository.posts.firstOrNull { it.id == postId }

            if(post!=null){
                CardDetail(
                    post = post,
                    onBookmarkClick = {
                        val index = PostRepository.posts.indexOfFirst{it.id==post.id}
                        if(index>=0){
                            val updated = post.copy(isBookmarked = !post.isBookmarked)
                            PostRepository.replacePost(index,updated)
                        }
                    },
                    onJoinClick = { // 인원이 초과했는지 여부는 PostRepository에서 체크
                        val user = UserProfile(
                            profileImage = profileUri,
                            name = nickname,
                            gender = gender,
                            age = age
                        )
                        PostRepository.addParticipant(post.id, user)
                    },
                    navController = navController,
                    user = UserProfile(profileUri, nickname, gender, age)
                )
            }
        }
        composable(route = "new_post",){
            NewPost(
                navController = navController,
                writer = UserProfile(profileUri, nickname, gender, age)
            )
        }
    }
}

