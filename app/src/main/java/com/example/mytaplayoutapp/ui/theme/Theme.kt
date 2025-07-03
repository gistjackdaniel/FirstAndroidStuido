package com.example.mytaplayoutapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ✅ Light Mode 색상 설정 (앱의 기본 색상 팔레트)
private val LightColorScheme = lightColorScheme(
    primary = Primary,             // 주색상 (버튼, 강조 영역)
    onPrimary = OnPrimary,         // 주색상 위 글자색

    secondary = Secondary,         // 보조색상 (보조 버튼 등)
    onSecondary = OnSecondary,     // 보조색상 위 글자색

    background = Background,       // 앱 배경색
    onBackground = OnBackground,   // 배경 위 글자색

    surface = Surface,             // 카드/컨테이너 표면 색상
    onSurface = OnSurface,         // 표면 위 글자색

    error = Error                  // 오류 색상
)

// ✅ Dark Mode 색상 설정 (현재 Light와 동일하게 설정 → 필요 시 커스터마이징 가능)
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    error = Error
)

/**
 * ✅ KoreaPassTheme 함수 (앱 전체 테마 적용 함수)
 * - Light/Dark 모드에 따라 색상 테마 자동 적용
 * - MaterialTheme을 통해 앱 전역에 테마 적용 가능
 *
 * @param darkTheme 시스템 다크 모드 여부 (기본값: 시스템 설정 기준)
 * @param content Composable UI Content
 */
@Composable
fun KoreaPassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Typography.kt에서 따로 정의 가능 (현재는 생략)
        content = content
    )
}
