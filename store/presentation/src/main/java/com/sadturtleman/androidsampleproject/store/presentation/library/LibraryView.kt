package com.sadturtleman.androidsampleproject.store.presentation.library

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumEmptyView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIconButton
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTab
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTabBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme

/**
 * 보관함 화면 (Figma: 최종 → 06 · 보관함).
 *
 * 화면 크롬(제목 줄 · 하단 탭바)과 [LibraryUiState] 분기를 담당하고,
 * 본문은 [LibraryContent] 에 맡긴다.
 * 아직 ViewModel 을 연결하지 않은 view-only 단계라 상태와 콜백을 모두 인자로 받는다.
 */
@Composable
fun LibraryView(
    state: LibraryUiState,
    onLayoutToggle: () -> Unit,
    onSortClick: () -> Unit,
    onItemClick: (ArtifactUiModel) -> Unit,
    onItemSaveClick: (ArtifactUiModel) -> Unit,
    onExploreClick: () -> Unit,
    onRetry: () -> Unit,
    onTabSelect: (MuseumTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuseumTheme.colors.bgCanvas),
    ) {
        LibraryTopBar(
            layout = (state as? LibraryUiState.Success)?.layout,
            onLayoutToggle = onLayoutToggle,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                LibraryUiState.Loading -> MuseumLoadingView()

                is LibraryUiState.Error -> MuseumErrorView(
                    message = state.message,
                    onRetry = onRetry,
                )

                is LibraryUiState.Success -> if (state.isEmpty) {
                    MuseumEmptyView(
                        title = "보관함이 비어 있습니다",
                        description = "마음에 드는 소장품을 저장해보세요.",
                        icon = MuseumIcons.Bookmark,
                        actionLabel = "소장품 둘러보기",
                        onAction = onExploreClick,
                    )
                } else {
                    LibraryContent(
                        state = state,
                        onSortClick = onSortClick,
                        onItemClick = onItemClick,
                        onItemSaveClick = onItemSaveClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        MuseumTabBar(
            selected = MuseumTab.Library,
            onSelect = onTabSelect,
        )
    }
}

/**
 * 제목 + 목록/그리드 전환 (Figma: 06 · 보관함 → row).
 *
 * 시안은 grid 아이콘 하나만 보여주지만, 전환 상태를 알 수 있도록
 * 리스트일 때는 grid, 그리드일 때는 리스트 아이콘을 띄운다.
 */
@Composable
private fun LibraryTopBar(
    layout: LibraryLayout?,
    onLayoutToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(MuseumTheme.size.appBarHeight)
            .padding(start = TITLE_PADDING_START, end = MuseumTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "보관함",
            style = MuseumTheme.typography.titleL,
            color = MuseumTheme.colors.textPrimary,
        )
        if (layout != null) {
            MuseumIconButton(
                icon = if (layout == LibraryLayout.List) MuseumIcons.Grid else MuseumIcons.Layers,
                contentDescription = if (layout == LibraryLayout.List) {
                    "그리드로 보기"
                } else {
                    "목록으로 보기"
                },
                onClick = onLayoutToggle,
            )
        }
    }
}

private val TITLE_PADDING_START = 20.dp

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Composable
private fun LibraryViewPreviewHost(state: LibraryUiState) {
    EveryMuseumTheme {
        LibraryView(
            state = state,
            onLayoutToggle = {},
            onSortClick = {},
            onItemClick = {},
            onItemSaveClick = {},
            onExploreClick = {},
            onRetry = {},
            onTabSelect = {},
        )
    }
}

@Preview(name = "보관함 · 목록 · Light", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun LibraryViewListPreview() {
    LibraryViewPreviewHost(LibraryPreviewData.success)
}

@Preview(
    name = "보관함 · 목록 · Dark",
    device = PREVIEW_DEVICE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun LibraryViewListDarkPreview() {
    LibraryViewPreviewHost(LibraryPreviewData.success)
}

@Preview(name = "보관함 · 그리드", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun LibraryViewGridPreview() {
    LibraryViewPreviewHost(LibraryPreviewData.success.copy(layout = LibraryLayout.Grid))
}

@Preview(name = "보관함 · 로딩", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun LibraryViewLoadingPreview() {
    LibraryViewPreviewHost(LibraryUiState.Loading)
}

@Preview(name = "보관함 · 에러", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun LibraryViewErrorPreview() {
    LibraryViewPreviewHost(LibraryUiState.Error("저장 목록을 불러오지 못했습니다."))
}

@Preview(name = "보관함 · 비어 있음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun LibraryViewEmptyPreview() {
    LibraryViewPreviewHost(LibraryPreviewData.success.copy(items = emptyList()))
}

/** iPhone 16 시안(393×852)에 대응하는 안드로이드 기준 기기. */
private const val PREVIEW_DEVICE = "spec:width=393dp,height=852dp,dpi=440"

private object LibraryPreviewData {

    val success = LibraryUiState.Success(
        sort = "최근 저장순",
        layout = LibraryLayout.List,
        items = listOf(
            ArtifactUiModel(
                id = "1",
                nameKr = "대당평일백제비 탑본",
                museum = "국립중앙박물관 · 본관",
                spec = "종이 · 세로 20.9cm, 가로 14.9cm",
                era = "통일신라",
                type = ArtifactType.Book,
            ),
            ArtifactUiModel(
                id = "2",
                nameKr = "백자 달항아리",
                museum = "국립중앙박물관 · 신수",
                spec = "백자 · 높이 44cm",
                era = "조선",
                designation = "보물",
                type = ArtifactType.Pottery,
            ),
            ArtifactUiModel(
                id = "3",
                nameKr = "금동미륵보살반가사유상",
                museum = "국립중앙박물관",
                spec = "금동 · 높이 93.5cm",
                era = "삼국",
                designation = "국보",
                type = ArtifactType.Metal,
            ),
            ArtifactUiModel(
                id = "4",
                nameKr = "사직단국왕친향도병풍",
                museum = "국립중앙박물관 · 신수",
                spec = "견 · 세로 127cm, 가로 50cm",
                era = "조선",
                type = ArtifactType.Painting,
            ),
            ArtifactUiModel(
                id = "5",
                nameKr = "석조여래좌상",
                museum = "국립경주박물관",
                spec = "화강암 · 높이 108cm",
                era = "통일신라",
                type = ArtifactType.Stone,
            ),
        ),
    )
}
