package com.sadturtleman.androidsampleproject.search.presentation.filter

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumChip
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumFilterOptionRow
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 필터 시트 본문 (Figma: 04 · 필터 → 분류 코드 탭 · 옵션).
 *
 * 탭 줄은 고정되고 옵션 목록만 스크롤한다.
 */
@Composable
fun FilterContent(
    state: FilterUiState.Success,
    onTabSelect: (FilterTabUiModel) -> Unit,
    onOptionToggle: (FilterOptionUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = MuseumTheme.spacing.sm),
    ) {
        item(key = "tabs") {
            FilterTabRow(
                tabs = state.tabs,
                selectedTabCode = state.selectedTabCode,
                onTabSelect = onTabSelect,
            )
        }

        items(state.options, key = { it.code }) { option ->
            MuseumFilterOptionRow(
                label = option.label,
                code = option.code,
                selected = option.code in state.selectedCodes,
                onClick = { onOptionToggle(option) },
                modifier = Modifier.padding(horizontal = MuseumTheme.spacing.lg),
            )
        }
    }
}

/** 분류 코드 탭 줄 (Figma: 04 · 필터 → 분류 코드 탭). */
@Composable
internal fun FilterTabRow(
    tabs: List<FilterTabUiModel>,
    selectedTabCode: String?,
    onTabSelect: (FilterTabUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = TAB_ROW_PADDING_HORIZONTAL)
            .padding(bottom = MuseumTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
    ) {
        tabs.forEach { tab ->
            MuseumChip(
                label = tab.label,
                selected = tab.parentCode == selectedTabCode,
                onClick = { onTabSelect(tab) },
            )
        }
    }
}

/** 시트 헤더와 같은 좌우 여백(20dp)을 쓴다. */
private val TAB_ROW_PADDING_HORIZONTAL = 20.dp
