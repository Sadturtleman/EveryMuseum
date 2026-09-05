package com.sadturtleman.androidsampleproject.search.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumChip
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcon
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumSectionHeader
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.debouncedClickable
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 검색 본문 (Figma: 02 · 검색 → scroll).
 *
 * 최근 검색어 · 인기 색인어 · 코드로 둘러보기 세 블록으로 구성된다.
 * 비어 있는 블록은 그리지 않는다.
 */
@Composable
internal fun SearchContent(
    state: SearchUiState.Success,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = MuseumTheme.spacing.lg,
            vertical = MuseumTheme.spacing.lg,
        ),
    ) {
        if (state.recentQueries.isNotEmpty()) {
            item(key = "recentHeader") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "최근 검색어",
                        style = MuseumTheme.typography.titleM,
                        color = MuseumTheme.colors.textPrimary,
                    )
                    Text(
                        text = "전체 삭제",
                        modifier = Modifier.debouncedClickable {
                            onIntent(SearchIntent.ClearRecentQueries)
                        },
                        style = MuseumTheme.typography.labelM,
                        color = MuseumTheme.colors.textTertiary,
                    )
                }
            }

            items(state.recentQueries.size, key = { state.recentQueries[it] }) { index ->
                val query = state.recentQueries[index]
                RecentQueryRow(
                    query = query,
                    onClick = { onIntent(SearchIntent.ClickRecentQuery(query)) },
                    onRemove = { onIntent(SearchIntent.RemoveRecentQuery(query)) },
                    modifier = Modifier.padding(top = if (index == 0) BLOCK_GAP else 0.dp),
                )
            }
        }

        if (state.popularIndexWords.isNotEmpty()) {
            item(key = "indexWords") {
                Column(
                    modifier = Modifier.padding(
                        top = if (state.recentQueries.isEmpty()) 0.dp else SECTION_GAP,
                    ),
                ) {
                    MuseumSectionHeader(title = "인기 색인어")
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = BLOCK_GAP),
                        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.sm),
                    ) {
                        state.popularIndexWords.forEach { word ->
                            MuseumChip(
                                label = word,
                                selected = false,
                                onClick = { onIntent(SearchIntent.ClickIndexWord(word)) },
                            )
                        }
                    }
                }
            }
        }

        if (state.codeCategories.isNotEmpty()) {
            item(key = "codeHeader") {
                MuseumSectionHeader(
                    title = "코드로 둘러보기",
                    modifier = Modifier.padding(top = SECTION_GAP),
                )
            }

            items(state.codeCategories.size, key = { state.codeCategories[it].parentCode }) { index ->
                CodeCategoryRow(
                    category = state.codeCategories[index],
                    onClick = {
                        onIntent(SearchIntent.ClickCodeCategory(state.codeCategories[index]))
                    },
                    showDivider = index != state.codeCategories.lastIndex,
                )
            }
        }
    }
}

/** 최근 검색어 한 줄 (Figma: 02 · 검색 → 최근 검색어 → row). */
@Composable
private fun RecentQueryRow(
    query: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .debouncedClickable(onClick = onClick)
            .padding(vertical = MuseumTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(ROW_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MuseumIcon(
            id = MuseumIcons.Clock,
            contentDescription = null,
            tint = MuseumTheme.colors.iconSecondary,
            size = RECENT_ICON_SIZE,
        )
        Text(
            text = query,
            modifier = Modifier.weight(1f),
            style = MuseumTheme.typography.bodyM,
            color = MuseumTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        MuseumIcon(
            id = MuseumIcons.Close,
            contentDescription = "$query 검색 기록 삭제",
            modifier = Modifier.debouncedClickable(onClick = onRemove),
            tint = MuseumTheme.colors.iconSecondary,
            size = MuseumTheme.size.iconXs,
        )
    }
}

/** 코드로 둘러보기 한 줄 (Figma: 02 · 검색 → 코드로 둘러보기 → row). */
@Composable
private fun CodeCategoryRow(
    category: CodeCategoryUiModel,
    onClick: () -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    val dividerColor = MuseumTheme.colors.borderSubtle

    Row(
        modifier = modifier
            .fillMaxWidth()
            .debouncedClickable(onClick = onClick)
            .drawBehind {
                if (!showDivider) return@drawBehind
                val stroke = 1.dp.toPx()
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, size.height - stroke / 2f),
                    end = Offset(size.width, size.height - stroke / 2f),
                    strokeWidth = stroke,
                )
            }
            .padding(vertical = MuseumTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(CODE_ICON_BOX_SIZE)
                .clip(MuseumTheme.shapes.sm)
                .background(MuseumTheme.colors.bgSurfaceSunken),
            contentAlignment = Alignment.Center,
        ) {
            MuseumIcon(
                id = category.icon,
                contentDescription = null,
                tint = MuseumTheme.colors.iconPrimary,
                size = MuseumTheme.size.iconSm,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xxs),
        ) {
            Text(
                text = category.title,
                style = MuseumTheme.typography.labelL,
                color = MuseumTheme.colors.textPrimary,
            )
            Text(
                text = category.summary,
                style = MuseumTheme.typography.labelS,
                color = MuseumTheme.colors.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        MuseumIcon(
            id = MuseumIcons.ChevronRight,
            contentDescription = null,
            tint = MuseumTheme.colors.iconSecondary,
            size = SMALL_CHEVRON_SIZE,
        )
    }
}

/** 블록(최근 검색어 · 인기 색인어 · 코드로 둘러보기) 사이 간격. */
private val SECTION_GAP = 28.dp

/** 블록 헤더와 첫 항목 사이 간격. */
private val BLOCK_GAP = 10.dp

private val ROW_GAP = 10.dp
private val RECENT_ICON_SIZE = 18.dp
private val SMALL_CHEVRON_SIZE = 18.dp
private val CODE_ICON_BOX_SIZE = 40.dp
