// 파일 경로: app/src/main/java/com/example/mytablayoutapp/ui/TabItem.kt
package customUi // 파일이 위치한 패키지 경로

import androidx.compose.material.icons.Icons // Material Design 아이콘 세트
import androidx.compose.material.icons.filled.Contacts // 연락처 아이콘
import androidx.compose.material.icons.filled.PhotoLibrary // 갤러리 아이콘
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.graphics.vector.ImageVector // 벡터 아이콘을 위한 타입

/**
 * 각 탭의 정보를 나타내는 sealed class 입니다.
 * sealed class는 미리 정의된 특정 타입들만 상속받을 수 있도록 제한합니다.
 *
 * @property title 탭의 제목으로 표시될 문자열
 * @property icon 탭에 표시될 아이콘 (ImageVector 타입)
 * @property route 내비게이션 시스템에서 화면을 식별하는 경로 문자열
 */
sealed class TabItem(val title: String, val icon: ImageVector, val route: String) {
    // object 키워드를 사용하여 각 탭을 싱글톤 인스턴스로 정의합니다.
    object Home : TabItem(
        title = "홈",
        icon = Icons.Filled.Contacts, // 미리 정의된 연락처 아이콘
        route = "home_screen" // 이 탭 화면으로 이동하기 위한 고유 경로 이름
    )

    object Gallery : TabItem(
        title = "갤러리",
        icon = Icons.Filled.PhotoLibrary, // 미리 정의된 갤러리 아이콘
        route = "gallery_screen"
    )


    object Profile : TabItem(
        title = "프로필",
        icon = Icons.Filled.AccountCircle, // 미리 정의된 원형 프로필 아이콘
        route = "profile_screen" // 중요: Profile 탭의 실제 라우트 이름으로 변경해야 합니다.
        // 현재 "free_theme_screen"으로 되어 있어 FreeTheme과 동일한 화면을 가리킵니다.
        // MainActivity.kt의 AppNavigation에서 ProfileScreen()에 연결된 라우트와 일치시켜야 합니다.
    )


}