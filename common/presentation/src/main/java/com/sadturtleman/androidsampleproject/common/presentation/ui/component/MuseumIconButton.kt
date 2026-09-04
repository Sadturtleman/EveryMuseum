package com.sadturtleman.androidsampleproject.common.presentation.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/** 아이콘 버튼 스타일 (Figma: C06 · IconButton → Style). */
enum class MuseumIconButtonStyle {
    /** 툴바 · 앱바용. 배경 없음. */
    Plain,

    /** 보조 액션. 가라앉은 표면 배경. */
    Filled,

    /** 유물 이미지 위에 올라가는 저장 · 공유 버튼. */
    Overlay,
}

/**
 * 아이콘 전용 버튼 (Figma: C06 · IconButton).
 *
 * 기본 크기는 `size/touch/min` 44dp 로, 최소 터치 영역을 항상 만족한다.
 * 카드 위 저장 버튼처럼 더 작게 써야 하면 [buttonSize] 로 줄인다.
 *
 * @param tint 아이콘 색을 직접 지정한다. null 이면 [style] 에서 유도한다.
 *  히어로 이미지 위 투명 앱바처럼 배경 없이 색만 바꿔야 할 때 쓴다.
 */
@Composable
fun MuseumIconButton(
    @DrawableRes icon: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: MuseumIconButtonStyle = MuseumIconButtonStyle.Plain,
    enabled: Boolean = true,
    buttonSize: Dp = MuseumTheme.size.touchMin,
    tint: Color? = null,
) {
    val colors = MuseumTheme.colors
    val shape = MuseumTheme.shapes.full
    val container = when (style) {
        MuseumIconButtonStyle.Plain -> Color.Transparent
        MuseumIconButtonStyle.Filled -> colors.bgSurfaceSunken
        MuseumIconButtonStyle.Overlay -> colors.bgScrim
    }
    val resolvedTint = tint ?: when (style) {
        MuseumIconButtonStyle.Plain, MuseumIconButtonStyle.Filled -> colors.iconPrimary
        MuseumIconButtonStyle.Overlay -> colors.iconInverse
    }

    Box(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .size(buttonSize)
            .clip(shape)
            .background(container)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        MuseumIcon(
            id = icon,
            contentDescription = contentDescription,
            tint = resolvedTint,
        )
    }
}

@Preview(name = "IconButton · Light", showBackground = true, backgroundColor = 0xFFF7F4EE)
@Preview(
    name = "IconButton · Dark",
    showBackground = true,
    backgroundColor = 0xFF14110F,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun MuseumIconButtonPreview() {
    EveryMuseumTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MuseumIconButton(MuseumIcons.Filter, "필터", {}, style = MuseumIconButtonStyle.Plain)
            MuseumIconButton(MuseumIcons.Sort, "정렬", {}, style = MuseumIconButtonStyle.Filled)
            MuseumIconButton(MuseumIcons.Bookmark, "저장", {}, style = MuseumIconButtonStyle.Overlay)
        }
    }
}
