package com.sadturtleman.androidsampleproject.store.presentation.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.CollectionCard
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.CollectionListItem
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 보관함 본문 (Figma: 06 · 보관함 → scroll).
 *
 * 저장 건수 · 정렬 줄과 목록이 함께 스크롤된다.
 * [LibraryLayout] 에 따라 리스트와 2열 그리드를 전환한다.
 */
@Composable
fun LibraryContent(
    state: LibraryUiState.Success,
    onSortClick: () -> Unit,
    onItemClick: (ArtifactUiModel) -> Unit,
    onItemSaveClick: (ArtifactUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = MuseumTheme.spacing.lg,
            vertical = 0.dp,
        ),
    ) {
        item(key = "count") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MuseumTheme.spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(MuseumTheme.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${state.items.size}",
                    style = MuseumTheme.typography.labelL.copy(fontWeight = FontWeight.Bold),
                    color = MuseumTheme.colors.textBrand,
                )
                Text(
                    text = "점 저장됨",
                    modifier = Modifier.weight(1f),
                    style = MuseumTheme.typography.bodyS,
                    color = MuseumTheme.colors.textSecondary,
                )
                Text(
                    text = state.sort,
                    modifier = Modifier.clickable(onClick = onSortClick),
                    style = MuseumTheme.typography.labelM,
                    color = MuseumTheme.colors.textSecondary,
                )
            }
        }

        when (state.layout) {
            LibraryLayout.List -> items(state.items, key = { it.id }) { item ->
                CollectionListItem(
                    nameKr = item.nameKr,
                    meta = item.museum,
                    spec = item.spec,
                    artifactType = item.type,
                    onClick = { onItemClick(item) },
                )
            }

            LibraryLayout.Grid -> {
                val rows = state.items.chunked(GRID_COLUMNS)
                itemsIndexed(rows, key = { _, row -> row.first().id }) { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = if (index == 0) MuseumTheme.spacing.md else GRID_ROW_GAP),
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
    }
}

private const val GRID_COLUMNS = 2
private val GRID_ROW_GAP = 20.dp
private val GRID_COLUMN_GAP = 12.dp
