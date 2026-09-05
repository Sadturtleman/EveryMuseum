package com.sadturtleman.androidsampleproject.search.presentation.result

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactImage
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.CollectionCard
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumChip
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcon
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.debouncedClickable
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 검색 결과 본문 (Figma: 03 · 검색 결과 → toolbar · 적용된 필터 · results).
 *
 * 건수 · 정렬 툴바와 적용된 필터 줄은 그리드와 함께 스크롤된다.
 *
 * 그리드는 [items] 를 두 칸씩 끊어 그린다. 칸을 채우려고 [items] 를 인덱스로 읽는 것이
 * 다음 페이지를 불러오는 신호가 되므로, 스크롤이 바닥에 가까워지면 Paging 이 알아서 이어 붙인다.
 */
@Composable
internal fun SearchResultContent(
    state: SearchResultUiState,
    items: LazyPagingItems<ArtifactUiModel>,
    onIntent: (SearchResultIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rowCount = (items.itemCount + GRID_COLUMNS - 1) / GRID_COLUMNS

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = MuseumTheme.spacing.lg),
    ) {
        item(key = "toolbar") {
            SearchResultToolbar(totalCount = state.totalCount)
        }

        item(key = "filters") {
            AppliedFilterRow(
                filters = state.appliedFilters,
                onFilterClick = { onIntent(SearchResultIntent.OpenFilter) },
                onFilterRemove = { filter ->
                    onIntent(SearchResultIntent.RemoveFilter(filter))
                },
            )
        }

        items(rowCount) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MuseumTheme.spacing.lg)
                    .padding(top = if (rowIndex == 0) 0.dp else GRID_ROW_GAP),
                horizontalArrangement = Arrangement.spacedBy(GRID_COLUMN_GAP),
            ) {
                repeat(GRID_COLUMNS) { column ->
                    val index = rowIndex * GRID_COLUMNS + column
                    // 마지막 줄이 홀수면 남은 칸을 비워 카드 폭을 유지한다.
                    // LazyPagingItems 는 범위 밖 인덱스에 null 이 아니라 예외를 던지므로 먼저 거른다.
                    val item = if (index < items.itemCount) items[index] else null
                    if (item == null) {
                        Spacer(modifier = Modifier.weight(1f))
                        return@repeat
                    }
                    CollectionCard(
                        modifier = Modifier.weight(1f),
                        nameKr = item.nameKr,
                        meta = item.museum,
                        artifactType = item.type,
                        designationName1 = item.designation,
                        nationalityName2 = item.era,
                        onClick = { onIntent(SearchResultIntent.ClickItem(item.id)) },
                        onSaveClick = { onIntent(SearchResultIntent.ToggleSave(item)) },
                        saved = item.saved,
                        image = { ArtifactImage(item.imageUrl, item.nameKr) },
                    )
                }
            }
        }

        // 다음 페이지를 불러오는 중 · 실패. 이미 그려진 목록은 그대로 두고 바닥에만 표시한다.
        when (val append = items.loadState.append) {
            is LoadState.Loading -> item(key = "appendLoading") {
                Box(modifier = Modifier.fillMaxWidth().height(APPEND_FOOTER_HEIGHT)) {
                    MuseumLoadingView()
                }
            }

            is LoadState.Error -> item(key = "appendError") {
                AppendErrorRow(
                    message = append.error.message ?: "다음 페이지를 불러오지 못했습니다.",
                    onRetry = items::retry,
                )
            }

            else -> Unit
        }
    }
}

/** 다음 페이지 실패 줄. 화면 전체를 에러로 덮지 않고 바닥에서만 다시 시도하게 한다. */
@Composable
private fun AppendErrorRow(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .debouncedClickable(onClick = onRetry)
            .padding(MuseumTheme.spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            modifier = Modifier.weight(1f),
            style = MuseumTheme.typography.bodyS,
            color = MuseumTheme.colors.textSecondary,
        )
        Text(
            text = "다시 시도",
            style = MuseumTheme.typography.labelM,
            color = MuseumTheme.colors.textBrand,
        )
    }
}

/**
 * 건수 + 정렬 (Figma: 03 · 검색 결과 → toolbar).
 *
 * 정렬은 라벨만 그린다. 목록 API 에 정렬 파라미터가 없어 클라이언트가 바꿀 수 있는 건
 * 이미 받아 온 페이지 안의 순서뿐인데, 페이지를 이어 붙이는 목록에서 그건 틀린 정렬이 된다.
 */
@Composable
private fun SearchResultToolbar(
    totalCount: Int?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = MuseumTheme.spacing.lg,
                end = MuseumTheme.spacing.lg,
                top = TOOLBAR_PADDING_TOP,
                bottom = TOOLBAR_PADDING_BOTTOM,
            ),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 건수는 목록과 따로 세므로 아직 없을 수 있다. 그때는 줄을 비워 둔다.
            if (totalCount != null) {
                Text(
                    text = "$totalCount",
                    style = MuseumTheme.typography.labelL.copy(fontWeight = FontWeight.Bold),
                    color = MuseumTheme.colors.textBrand,
                )
                Text(
                    text = "건",
                    style = MuseumTheme.typography.bodyS,
                    color = MuseumTheme.colors.textSecondary,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MuseumIcon(
                id = MuseumIcons.Sort,
                contentDescription = null,
                tint = MuseumTheme.colors.iconSecondary,
                size = MuseumTheme.size.iconXs,
            )
            Text(
                text = SORT_LABEL,
                style = MuseumTheme.typography.labelM,
                color = MuseumTheme.colors.textSecondary,
            )
        }
    }
}

/**
 * 필터 진입 pill + 적용된 필터 칩 (Figma: 03 · 검색 결과 → 적용된 필터).
 *
 * 칩을 누르면 해당 필터가 해제된다.
 */
@Composable
private fun AppliedFilterRow(
    filters: List<AppliedFilterUiModel>,
    onFilterClick: () -> Unit,
    onFilterRemove: (AppliedFilterUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = MuseumTheme.spacing.lg)
            .padding(bottom = MuseumTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(MuseumTheme.shapes.full)
                .background(MuseumTheme.colors.bgSurfaceInverse)
                .debouncedClickable(onClick = onFilterClick)
                .padding(
                    horizontal = MuseumTheme.spacing.md,
                    vertical = MuseumTheme.spacing.sm,
                ),
            horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MuseumIcon(
                id = MuseumIcons.Filter,
                contentDescription = null,
                tint = MuseumTheme.colors.textInverse,
                size = MuseumTheme.size.iconXs,
            )
            Text(
                text = if (filters.isEmpty()) "필터" else "필터 ${filters.size}",
                style = MuseumTheme.typography.labelM,
                color = MuseumTheme.colors.textInverse,
            )
        }

        filters.forEach { filter ->
            MuseumChip(
                label = filter.label,
                selected = true,
                onClick = { onFilterRemove(filter) },
            )
        }
    }
}

/** 서버가 준 순서 그대로. 목록 API 는 정렬 기준을 노출하지 않는다. */
private const val SORT_LABEL = "정확도순"

private const val GRID_COLUMNS = 2
private val GRID_ROW_GAP = 20.dp
private val GRID_COLUMN_GAP = 12.dp
private val TOOLBAR_PADDING_TOP = 6.dp
private val TOOLBAR_PADDING_BOTTOM = 10.dp
private val APPEND_FOOTER_HEIGHT = 72.dp
