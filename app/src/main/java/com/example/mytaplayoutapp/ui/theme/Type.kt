package com.example.mytaplayoutapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ✅ 앱 전용 폰트 패밀리 설정 (여기서는 기본 시스템 폰트 사용)
// 나중에 Google Fonts 연동 가능 (예: Noto Sans)
val AppFontFamily = FontFamily.Default

/**
 * ✅ KoreaPass 앱의 타이포그래피 시스템
 * - 앱의 분위기에 맞춰서 친근하고 읽기 쉬운 스타일로 구성
 * - Material3 Typography 시스템을 따름
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,   // 가장 강조되는 제목 (예: 앱 메인 제목)
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    displayMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium, // 섹션 제목 등
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal, // 기본 본문 텍스트
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal, // 보조 본문
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium, // 버튼 텍스트 등 강조용
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal, // 캡션, 작은 안내 문구
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
