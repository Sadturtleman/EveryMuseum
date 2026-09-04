package com.sadturtleman.androidsampleproject.common.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

val LocalMuseumColors = staticCompositionLocalOf { museumLightColors() }
val LocalMuseumTypography = staticCompositionLocalOf { museumTypography() }
val LocalMuseumSpacing = staticCompositionLocalOf { MuseumSpacing() }
val LocalMuseumShapes = staticCompositionLocalOf { MuseumShapes() }
val LocalMuseumSize = staticCompositionLocalOf { MuseumSize() }
val LocalMuseumElevation = staticCompositionLocalOf { MuseumElevation() }

/**
 * 디자인 토큰 접근점. 화면 코드는 `MuseumTheme.colors.textPrimary` 처럼 쓴다.
 *
 * Material3 의 [MaterialTheme] 도 함께 설정되므로 M3 컴포넌트를 섞어 써도
 * 브랜드 색이 유지된다. 다만 새 코드는 이 토큰을 우선 사용한다.
 */
object MuseumTheme {

    val colors: MuseumColors
        @Composable @ReadOnlyComposable get() = LocalMuseumColors.current

    val typography: MuseumTypography
        @Composable @ReadOnlyComposable get() = LocalMuseumTypography.current

    val spacing: MuseumSpacing
        @Composable @ReadOnlyComposable get() = LocalMuseumSpacing.current

    val shapes: MuseumShapes
        @Composable @ReadOnlyComposable get() = LocalMuseumShapes.current

    val size: MuseumSize
        @Composable @ReadOnlyComposable get() = LocalMuseumSize.current

    val elevation: MuseumElevation
        @Composable @ReadOnlyComposable get() = LocalMuseumElevation.current
}

/**
 * EveryMuseum 디자인 시스템 테마.
 *
 * 안드로이드 12+ 의 dynamic color 는 의도적으로 지원하지 않는다.
 * 단청 주칠(vermilion)·청자(celadon)·금박(gold) 조합이 브랜드 정체성이라
 * 배경화면 색으로 치환되면 디자인 의도가 깨진다.
 */
@Composable
fun EveryMuseumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = remember(darkTheme) {
        if (darkTheme) museumDarkColors() else museumLightColors()
    }
    val typography = remember { museumTypography() }
    val spacing = remember { MuseumSpacing() }
    val shapes = remember { MuseumShapes() }
    val size = remember { MuseumSize() }
    val elevation = remember { MuseumElevation() }

    CompositionLocalProvider(
        LocalMuseumColors provides colors,
        LocalMuseumTypography provides typography,
        LocalMuseumSpacing provides spacing,
        LocalMuseumShapes provides shapes,
        LocalMuseumSize provides size,
        LocalMuseumElevation provides elevation,
        LocalContentColor provides colors.textPrimary,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(darkTheme),
            typography = typography.toMaterialTypography(),
            content = content,
        )
    }
}

/**
 * 이전 이름. `RootComposable` 등 기존 호출부가 깨지지 않도록 남겨 둔다.
 * 새 코드는 [EveryMuseumTheme] 를 사용한다.
 */
@Deprecated(
    message = "EveryMuseumTheme 로 대체되었습니다.",
    replaceWith = ReplaceWith("EveryMuseumTheme(darkTheme, content)"),
)
@Composable
fun AndroidSampleProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) = EveryMuseumTheme(darkTheme = darkTheme, content = content)

/**
 * 시맨틱 토큰을 Material3 [androidx.compose.material3.ColorScheme] 에 얹는다.
 * 디자인 시스템에 없는 슬롯(error 계열 등)은 M3 기본값을 그대로 둔다.
 */
private fun MuseumColors.toMaterialColorScheme(darkTheme: Boolean) =
    (if (darkTheme) darkColorScheme() else lightColorScheme()).copy(
        primary = bgBrand,
        onPrimary = textOnBrand,
        primaryContainer = bgBrandSubtle,
        onPrimaryContainer = textBrand,
        secondary = textAccent,
        onSecondary = textOnBrand,
        secondaryContainer = bgAccentSubtle,
        onSecondaryContainer = textAccent,
        tertiary = textGold,
        onTertiary = textOnBrand,
        tertiaryContainer = bgGoldSubtle,
        onTertiaryContainer = textGold,
        background = bgCanvas,
        onBackground = textPrimary,
        surface = bgSurface,
        onSurface = textPrimary,
        surfaceVariant = bgSurfaceSunken,
        onSurfaceVariant = textSecondary,
        outline = borderDefault,
        outlineVariant = borderSubtle,
        scrim = bgScrim,
    )

/** 타입 스케일을 Material3 슬롯에 대응시킨다. */
private fun MuseumTypography.toMaterialTypography() = Typography(
    displayLarge = displaySerif,
    displayMedium = displaySerif,
    displaySmall = titleSerifL,
    headlineLarge = titleSerifL,
    headlineMedium = titleSerifM,
    headlineSmall = titleL,
    titleLarge = titleL,
    titleMedium = titleM,
    titleSmall = headline,
    bodyLarge = bodyL,
    bodyMedium = bodyM,
    bodySmall = bodyS,
    labelLarge = labelL,
    labelMedium = labelM,
    labelSmall = labelS,
)
