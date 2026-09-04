package com.sadturtleman.androidsampleproject.search.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIconButton
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumSearchBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 뒤로가기 + 검색 입력으로 이루어진 상단 바 (Figma: 02 · 검색 / 03 · 검색 결과 → search header).
 *
 * 두 화면이 같은 구성을 쓰므로 모듈 안에서 공유한다.
 * 표준 [com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTopAppBar] 와 달리
 * 제목 자리에 검색 바가 들어가는 형태라 디자인 시스템에 올리지 않고 여기에 둔다.
 *
 * @param onQueryChange null 이면 입력을 받지 않고 [onSearchBarClick] 으로 넘긴다.
 */
@Composable
internal fun SearchTopBar(
    query: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onQueryChange: ((String) -> Unit)? = null,
    onSearch: () -> Unit = {},
    onClear: () -> Unit = {},
    onSearchBarClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(MuseumTheme.size.appBarHeight)
            .padding(start = LEADING_PADDING, end = MuseumTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(LEADING_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuseumIconButton(
            icon = MuseumIcons.ArrowLeft,
            contentDescription = "뒤로 가기",
            onClick = onBackClick,
        )
        MuseumSearchBar(
            value = query,
            onValueChange = onQueryChange ?: {},
            modifier = Modifier.weight(1f),
            onSearch = onSearch,
            onClear = onClear,
            onClick = onSearchBarClick,
        )
    }
}

/** Figma 상 뒤로가기 버튼 좌측 여백이자 버튼과 검색 바 사이 간격. */
private val LEADING_PADDING = 4.dp
