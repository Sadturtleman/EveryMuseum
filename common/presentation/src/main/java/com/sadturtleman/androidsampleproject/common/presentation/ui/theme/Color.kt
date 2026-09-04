package com.sadturtleman.androidsampleproject.common.presentation.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * EveryMuseum 원시 팔레트 (Figma: 01 · Foundations → Primitives).
 *
 * 먹(ink) 중립색 위에 단청 주칠(vermilion)을 브랜드 강조색으로,
 * 청자(celadon)와 금박(gold)을 보조색으로 사용한다.
 *
 * 화면 코드에서 이 값을 직접 쓰지 않는다. 반드시 [MuseumColors] 의 시맨틱 토큰을 통해 접근한다.
 * Figma 에서도 프리미티브는 픽커에 노출되지 않는다.
 */
internal object MuseumPalette {

    val Ink0 = Color(0xFFFFFFFF)
    val Ink50 = Color(0xFFF7F4EE)
    val Ink100 = Color(0xFFEFEAE2)
    val Ink200 = Color(0xFFE3DDD4)
    val Ink300 = Color(0xFFC9C0B5)
    val Ink400 = Color(0xFFADA398)
    val Ink500 = Color(0xFF8C8177)
    val Ink600 = Color(0xFF6B6157)
    val Ink700 = Color(0xFF4B423A)
    val Ink800 = Color(0xFF332D27)
    val Ink900 = Color(0xFF1E1A17)
    val Ink950 = Color(0xFF14110F)

    val Vermilion100 = Color(0xFFF9E8E4)
    val Vermilion200 = Color(0xFFEFC9C1)
    val Vermilion300 = Color(0xFFDE9C90)
    val Vermilion400 = Color(0xFFC96A5B)
    val Vermilion500 = Color(0xFFB34333)
    val Vermilion600 = Color(0xFF963527)
    val Vermilion700 = Color(0xFF7A2A1E)

    val Celadon100 = Color(0xFFE5F0EA)
    val Celadon300 = Color(0xFFAFCCBF)
    val Celadon400 = Color(0xFF7BA795)
    val Celadon500 = Color(0xFF4F806C)
    val Celadon600 = Color(0xFF3E6656)
    val Celadon700 = Color(0xFF2F4E43)

    val Gold100 = Color(0xFFF5EBD6)
    val Gold400 = Color(0xFFC9A961)
    val Gold500 = Color(0xFFB08D3E)
    val Gold600 = Color(0xFF8A6B2B)
}

/**
 * EveryMuseum 시맨틱 컬러 토큰 (Figma: 01 · Foundations → Semantic).
 *
 * 화면 코드는 항상 이 토큰만 사용한다. 라이트/다크 전환은 [museumLightColors] /
 * [museumDarkColors] 가 담당하므로 호출부에서 `isSystemInDarkTheme()` 을 분기할 필요가 없다.
 */
@Immutable
data class MuseumColors(
    /** 앱 최상위 배경 */
    val bgCanvas: Color,
    /** 카드 · 시트 표면 */
    val bgSurface: Color,
    /** 썸네일 · 입력 배경 */
    val bgSurfaceSunken: Color,
    /** 주요 버튼 배경 */
    val bgBrand: Color,
    /** 선택된 칩 배경 */
    val bgBrandSubtle: Color,
    /** 시대 태그 배경 */
    val bgAccentSubtle: Color,
    /** 국보 · 보물 뱃지 배경 */
    val bgGoldSubtle: Color,
    /** 이미지 위 오버레이 버튼 배경 */
    val bgScrim: Color,
    /** 반전 표면. 적용된 필터 pill 처럼 배경 위에서 튀어야 하는 요소 */
    val bgSurfaceInverse: Color,
    /** 유물명 · 본문 */
    val textPrimary: Color,
    /** 소장처 · 부가 정보 */
    val textSecondary: Color,
    /** 플레이스홀더 · 캡션 */
    val textTertiary: Color,
    /** 링크 · 강조 */
    val textBrand: Color,
    /** 시대 태그 텍스트 */
    val textAccent: Color,
    /** 국보 · 보물 뱃지 텍스트 */
    val textGold: Color,
    /** 브랜드 배경 위 텍스트 */
    val textOnBrand: Color,
    /** 반전 표면 위 텍스트 */
    val textInverse: Color,
    /** 항상 어두운 표면(큐레이션 히어로) 위 제목 */
    val textOnDark: Color,
    /** 항상 어두운 표면 위 보조 텍스트 */
    val textOnDarkMuted: Color,
    /** 구분선 */
    val borderSubtle: Color,
    /** 입력 · 칩 테두리 */
    val borderDefault: Color,
    /** 눌림 · 포커스 테두리 */
    val borderStrong: Color,
    /** 선택된 칩 테두리 */
    val borderBrand: Color,
    /** 기본 아이콘 */
    val iconPrimary: Color,
    /** 비활성 아이콘 */
    val iconSecondary: Color,
    /** 브랜드 아이콘 */
    val iconBrand: Color,
    /** 어두운 배경 위 아이콘 */
    val iconInverse: Color,
    /** 라이트 테마 여부. 시스템 바 아이콘 대비 등에 사용한다. */
    val isLight: Boolean,
)

/** Figma Color 변수의 Light 모드. */
fun museumLightColors(): MuseumColors = MuseumColors(
    bgCanvas = MuseumPalette.Ink50,
    bgSurface = MuseumPalette.Ink0,
    bgSurfaceSunken = MuseumPalette.Ink100,
    bgBrand = MuseumPalette.Vermilion600,
    bgBrandSubtle = MuseumPalette.Vermilion100,
    bgAccentSubtle = MuseumPalette.Celadon100,
    bgGoldSubtle = MuseumPalette.Gold100,
    bgScrim = MuseumPalette.Ink900,
    bgSurfaceInverse = MuseumPalette.Ink900,
    textPrimary = MuseumPalette.Ink900,
    textSecondary = MuseumPalette.Ink600,
    textTertiary = MuseumPalette.Ink500,
    textBrand = MuseumPalette.Vermilion600,
    textAccent = MuseumPalette.Celadon600,
    textGold = MuseumPalette.Gold600,
    textOnBrand = MuseumPalette.Ink0,
    textInverse = MuseumPalette.Ink0,
    textOnDark = MuseumPalette.Ink50,
    textOnDarkMuted = MuseumPalette.Ink400,
    borderSubtle = MuseumPalette.Ink200,
    borderDefault = MuseumPalette.Ink300,
    borderStrong = MuseumPalette.Ink800,
    borderBrand = MuseumPalette.Vermilion500,
    iconPrimary = MuseumPalette.Ink800,
    iconSecondary = MuseumPalette.Ink500,
    iconBrand = MuseumPalette.Vermilion600,
    iconInverse = MuseumPalette.Ink0,
    isLight = true,
)

/**
 * Figma Color 변수의 Dark 모드.
 *
 * bg 계열, text 계열, border/subtle·default, icon/primary·secondary 는 Foundations 의
 * Semantic 표에 Light/Dark 두 값이 모두 명시되어 있어 그대로 옮겼다.
 *
 * 다만 아래 네 토큰은 Foundations 표에 Dark 값이 표기되어 있지 않아
 * 같은 표의 대응 규칙(브랜드 계열은 한 단계 밝게, ink 계열은 스케일 반전)을 따라 채웠다.
 * Figma 에 Dark 값이 확정되면 이 네 줄만 교체하면 된다.
 *  - textAccent  : celadon-600 → celadon-300 (textBrand 의 600 → 300 규칙과 동일)
 *  - textGold    : gold-600 → gold-400
 *  - borderStrong: ink-800 → ink-200 (borderSubtle 200↔800, borderDefault 300↔700 과 대칭)
 *  - borderBrand : vermilion-500 → vermilion-400
 *
 * bgSurfaceInverse / textInverse 도 Foundations 표에 없는 값이라 "표면의 반대" 라는
 * 토큰 의미대로 라이트는 어둡게(ink-900 · 흰 글자), 다크는 밝게(ink-50 · ink-900 글자) 채웠다.
 */
fun museumDarkColors(): MuseumColors = MuseumColors(
    bgCanvas = MuseumPalette.Ink950,
    bgSurface = MuseumPalette.Ink900,
    bgSurfaceSunken = MuseumPalette.Ink950,
    bgBrand = MuseumPalette.Vermilion500,
    bgBrandSubtle = MuseumPalette.Vermilion700,
    bgAccentSubtle = MuseumPalette.Celadon700,
    bgGoldSubtle = MuseumPalette.Gold600,
    bgScrim = MuseumPalette.Ink900,
    bgSurfaceInverse = MuseumPalette.Ink50,
    textPrimary = MuseumPalette.Ink50,
    textSecondary = MuseumPalette.Ink300,
    textTertiary = MuseumPalette.Ink400,
    textBrand = MuseumPalette.Vermilion300,
    textAccent = MuseumPalette.Celadon300,
    textGold = MuseumPalette.Gold400,
    textOnBrand = MuseumPalette.Ink0,
    textInverse = MuseumPalette.Ink900,
    textOnDark = MuseumPalette.Ink50,
    textOnDarkMuted = MuseumPalette.Ink400,
    borderSubtle = MuseumPalette.Ink800,
    borderDefault = MuseumPalette.Ink700,
    borderStrong = MuseumPalette.Ink200,
    borderBrand = MuseumPalette.Vermilion400,
    iconPrimary = MuseumPalette.Ink100,
    iconSecondary = MuseumPalette.Ink400,
    iconBrand = MuseumPalette.Vermilion300,
    iconInverse = MuseumPalette.Ink0,
    isLight = false,
)
