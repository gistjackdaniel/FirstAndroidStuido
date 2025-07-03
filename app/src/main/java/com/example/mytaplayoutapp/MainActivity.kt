// 파일 경로: app/src/main/java/com/example/mytablayoutapp/MainActivity.kt
package com.example.mytablayoutapp // 여러분의 프로젝트 패키지 이름

import android.os.Bundle // 액티비티 상태 저장을 위한 번들
import androidx.activity.ComponentActivity // Compose를 사용하는 액티비티의 기본 클래스
import androidx.activity.compose.setContent // 액티비티의 컨텐츠를 Compose UI로 설정하는 함수
import androidx.activity.enableEdgeToEdge // 전체 화면 사용(엣지 투 엣지)을 활성화 (선택 사항)
import androidx.compose.foundation.layout.padding // UI 요소에 여백을 주기 위함
import androidx.compose.material3.* // Material 3 컴포넌트 전체 import
import androidx.compose.runtime.* // remember, mutableStateOf 등 Compose의 상태 관리 함수
import androidx.compose.ui.Modifier // UI 요소 수정을 위한 Modifier
import androidx.navigation.NavDestination.Companion.hierarchy // 내비게이션 계층 구조를 다루기 위함
import androidx.navigation.NavGraph.Companion.findStartDestination // 내비게이션 그래프의 시작점 찾기
import androidx.navigation.NavHostController // 내비게이션을 제어하는 컨트롤러
import androidx.navigation.compose.* // rememberNavController, NavHost, composable 등 Compose Navigation 관련 함수

// 3단계에서 만든 화면 Composable 함수들을 import 합니다.
import com.example.mytablayoutapp.customUi.contacts.ContactsScreen
import com.example.mytablayoutapp.customUi.gallery.GalleryScreen
import com.example.mytablayoutapp.customUi.freetheme.FreeThemeScreen
// 4단계에서 만든 TabItem sealed class를 import 합니다.
import com.example.mytablayoutapp.ui.TabItem
// 프로젝트의 테마를 import 합니다. (프로젝트 생성 시 자동 생성됨)
import com.example.mytaplayoutapp.ui.theme.KoreaPassTheme // 여러분의 프로젝트 테마 이름으로 변경

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 부모 클래스의 onCreate 호출
        enableEdgeToEdge() // 화면의 시스템 바 영역까지 앱 컨텐츠를 확장 (선택 사항)

        // setContent 블록 내부에 앱의 전체 UI를 Compose로 정의합니다.
        setContent {
            // KoreaPassTheme은 앱의 전반적인 스타일(색상, 글꼴 등)을 정의합니다.
            // 프로젝트 생성 시 자동으로 만들어진 이름을 사용하세요.
            KoreaPassTheme {
                // MainScreen Composable 함수를 호출하여 앱의 메인 화면을 표시합니다.
                MainScreen()
            }
        }
    }
}

/**
 * 앱의 메인 화면으로, 하단 탭 네비게이션과 각 탭에 해당하는 컨텐츠를 포함합니다.
 */
@OptIn(ExperimentalMaterial3Api::class) // 일부 Material 3 API는 실험적일 수 있음을 명시
@Composable
fun MainScreen() {
    // rememberNavController(): 내비게이션 상태를 기억하고 관리하는 NavController 인스턴스를 생성합니다.
    // 화면 회전 등 구성 변경에도 상태를 유지합니다.
    val navController = rememberNavController()

    // 표시할 탭들의 목록을 정의합니다. TabItem에서 정의한 객체들을 사용합니다.
    val tabs = listOf(
        TabItem.Contacts,
        TabItem.Gallery,
        TabItem.FreeTheme
    )

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
            modifier = Modifier.padding(innerPadding) // 패딩 적용
        )
    }
}

/**
 * NavHost를 사용하여 각 탭(라우트)에 맞는 화면(Composable)을 연결합니다.
 *
 * @param navController 내비게이션을 제어하는 컨트롤러
 * @param modifier 외부에서 전달받는 Modifier (여기서는 Scaffold의 패딩을 적용하기 위함)
 */
@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    // NavHost는 내비게이션 그래프의 컨테이너 역할을 합니다.
    // navController를 통해 현재 표시할 화면을 결정하고, startDestination으로 앱 시작 시 첫 화면을 지정합니다.
    NavHost(
        navController = navController,
        startDestination = TabItem.Contacts.route, // 앱 시작 시 "연락처" 탭을 기본으로 표시
        modifier = modifier // Scaffold로부터 전달받은 패딩 등을 적용
    ) {
        // composable(route) { Composable 함수 } 형식으로 각 라우트와 화면을 매핑합니다.
        composable(TabItem.Contacts.route) { // "contacts_screen" 경로일 때 ContactsScreen 표시
            ContactsScreen()
        }
        composable(TabItem.Gallery.route) { // "gallery_screen" 경로일 때 GalleryScreen 표시
            GalleryScreen()
        }
        composable(TabItem.FreeTheme.route) { // "free_theme_screen" 경로일 때 FreeThemeScreen 표시
            FreeThemeScreen()
        }
    }
}