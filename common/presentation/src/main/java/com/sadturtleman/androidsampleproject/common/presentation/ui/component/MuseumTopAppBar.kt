package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumFontFamily
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 하위 화면용 상단 앱바 (Figma: C15 · TopAppBar → Type=Default).
 *
 * 뒤로가기 + 제목 구성이다. 높이는 `size/appbar/height` 56dp.
 *
 * @param actions 우측 액션 슬롯. 비우면 아무것도 그리지 않는다.
 *  기본 아이콘 색은 `color/icon/primary` 이므로 [MuseumIconButton] 을 그대로 넣으면 된다.
 */
@Composable
fun MuseumTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    MuseumTopAppBarLayout(
        modifier = modifier,
        background = MuseumTheme.colors.bgCanvas,
        leading = {
            MuseumIconButton(
                icon = MuseumIcons.ArrowLeft,
                contentDescription = "뒤로 가기",
                onClick = onBackClick,
            )
            Text(
                text = title,
                style = MuseumTheme.typography.titleM,
                color = MuseumTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs),
        actions = actions,
    )
}

/**
 * 홈 화면용 상단 앱바 (Figma: C15 · TopAppBar → Type=Home).
 *
 * 브랜드 워드마크와 검색 · 보관함 진입을 제공한다.
 */
@Composable
fun MuseumHomeTopAppBar(
    onSearchClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
    brand: String = "EveryMuseum",
) {
    MuseumTopAppBarLayout(
        modifier = modifier,
        background = MuseumTheme.colors.bgCanvas,
        leading = {
            Text(
                text = brand,
                modifier = Modifier.padding(start = MuseumTheme.spacing.sm),
                style = MuseumTheme.typography.titleSerifM.copy(
                    fontFamily = MuseumFontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = BRAND_FONT_SIZE,
                    lineHeight = BRAND_LINE_HEIGHT,
                    letterSpacing = BRAND_LETTER_SPACING,
                ),
                color = MuseumTheme.colors.textPrimary,
            )
        },
        actions = {
            MuseumIconButton(
                icon = MuseumIcons.Search,
                contentDescription = "검색",
                onClick = onSearchClick,
            )
            MuseumIconButton(
                icon = MuseumIcons.Bookmark,
                contentDescription = "보관함",
                onClick = onBookmarkClick,
            )
        },
    )
}

/**
 * 상세 히어로 이미지 위에 얹히는 투명 앱바 (Figma: C15 · TopAppBar → Type=Overlay).
 *
 * 배경이 없고 아이콘은 `color/icon/inverse` 로 그려져 사진 위에서도 읽힌다.
 * 히어로 이미지 위에 겹쳐 배치해야 하므로 보통 `Box` 안에서 상단 정렬로 쓴다.
 */
@Composable
fun MuseumOverlayTopAppBar(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = MuseumTheme.colors.iconInverse
    MuseumTopAppBarLayout(
        modifier = modifier,
        background = Color.Transparent,
        leading = {
            MuseumIconButton(
                icon = MuseumIcons.ArrowLeft,
                contentDescription = "뒤로 가기",
                onClick = onBackClick,
                tint = tint,
            )
        },
        actions = {
            MuseumIconButton(
                icon = MuseumIcons.Share,
                contentDescription = "공유",
                onClick = onShareClick,
                tint = tint,
            )
            MuseumIconButton(
                icon = MuseumIcons.Bookmark,
                contentDescription = "보관함에 저장",
                onClick = onBookmarkClick,
                tint = tint,
            )
        },
    )
}

/** 세 variant 가 공유하는 뼈대. 높이 · 여백 · 정렬은 모두 동일하다. */
@Composable
private fun MuseumTopAppBarLayout(
    background: Color,
    leading: @Composable RowScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    leadingArrangement: Arrangement.Horizontal = Arrangement.Start,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(MuseumTheme.size.appBarHeight)
            .background(background)
            .padding(MuseumTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = leadingArrangement,
            verticalAlignment = Alignment.CenterVertically,
            content = leading,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
}

// 브랜드 워드마크는 타입 스케일에 없는 1회성 값이라 여기서만 정의한다.
// Figma: Noto Serif KR Bold · 21 / 150% / -1%
private val BRAND_FONT_SIZE = 21.sp
private val BRAND_LINE_HEIGHT = 31.5.sp
private val BRAND_LETTER_SPACING = (-0.01f).em

@Preview(name = "TopAppBar · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "TopAppBar · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MuseumTopAppBarPreview() {
    EveryMuseumTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MuseumTopAppBar(
                title = "검색 결과",
                onBackClick = {},
                actions = {
                    MuseumIconButton(MuseumIcons.More, "더보기", {})
                },
            )
            MuseumHomeTopAppBar(onSearchClick = {}, onBookmarkClick = {})
            Row(modifier = Modifier.background(Color(0xFF4B423A))) {
                MuseumOverlayTopAppBar(
                    onBackClick = {},
                    onShareClick = {},
                    onBookmarkClick = {},
                )
            }
        }
    }
}
