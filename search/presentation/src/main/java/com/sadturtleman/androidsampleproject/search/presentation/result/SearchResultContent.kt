package com.sadturtleman.androidsampleproject.search.presentation.result

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.CollectionCard
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumChip
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcon
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 검색 결과 본문 (Figma: 03 · 검색 결과 → toolbar · 적용된 필터 · results).
 *
 * 건수 · 정렬 툴바와 적용된 필터 줄은 그리드와 함께 스크롤된다.
 */
@Composable
fun SearchResultContent(
    state: SearchResultUiState.Success,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    onFilterRemove: (AppliedFilterUiModel) -> Unit,
    onItemClick: (ArtifactUiModel) -> Unit,
    onItemSaveClick: (ArtifactUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = state.items.chunked(GRID_COLUMNS)

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = MuseumTheme.spacing.lg),
    ) {
        item(key = "toolbar") {
            SearchResultToolbar(
                totalCount = state.totalCount,
                sort = state.sort,
                onSortClick = onSortClick,
            )
        }

        item(key = "filters") {
            AppliedFilterRow(
                filters = state.appliedFilters,
                onFilterClick = onFilterClick,
                onFilterRemove = onFilterRemove,
            )
        }

        itemsIndexed(rows, key = { _, row -> row.first().id }) { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MuseumTheme.spacing.lg)
                    .padding(top = if (index == 0) 0.dp else GRID_ROW_GAP),
                horizontalArrangement = Arrangement.spacedBy(GRID_COLUMN_GAP),
            ) {
                row.forEach { item ->
                    CollectionCard(
                        modifier = Modifier.weight(1f),
                        nameKr = item.nameKr,
                        meta = item.museum,
                        artifactType = item.type,
                        designationName1 = item.designation,
                        nationalityName2 = item.era,
                        onClick = { onItemClick(item) },
                        onSaveClick = { onItemSaveClick(item) },
                    )
                }
                repeat(GRID_COLUMNS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/** 건수 + 정렬 (Figma: 03 · 검색 결과 → toolbar). */
@Composable
private fun SearchResultToolbar(
    totalCount: Int,
    sort: String,
    onSortClick: () -> Unit,
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
        Row(
            modifier = Modifier.clickable(onClick = onSortClick),
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
                text = sort,
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
                .clickable(onClick = onFilterClick)
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

private const val GRID_COLUMNS = 2
private val GRID_ROW_GAP = 20.dp
private val GRID_COLUMN_GAP = 12.dp
private val TOOLBAR_PADDING_TOP = 6.dp
private val TOOLBAR_PADDING_BOTTOM = 10.dp
