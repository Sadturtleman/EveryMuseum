package com.sadturtleman.androidsampleproject.search.presentation.result

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumEmptyView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTab
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTabBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme
import com.sadturtleman.androidsampleproject.search.presentation.component.SearchTopBar

/**
 * 검색 결과 화면 (Figma: 최종 → 03 · 검색 결과).
 *
 * 상단 검색 바와 [SearchResultUiState] 분기를 담당하고, 본문은 [SearchResultContent] 에 맡긴다.
 * 아직 ViewModel 을 연결하지 않은 view-only 단계라 상태와 콜백을 모두 인자로 받는다.
 */
@Composable
fun SearchResultView(
    state: SearchResultUiState,
    onBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onQueryClear: () -> Unit,
    onSortClick: () -> Unit,
    onFilterClick: () -> Unit,
    onFilterRemove: (AppliedFilterUiModel) -> Unit,
    onItemClick: (ArtifactUiModel) -> Unit,
    onItemSaveClick: (ArtifactUiModel) -> Unit,
    onRetry: () -> Unit,
    onTabSelect: (MuseumTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuseumTheme.colors.bgCanvas),
    ) {
        SearchTopBar(
            query = (state as? SearchResultUiState.Success)?.query.orEmpty(),
            onBackClick = onBackClick,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onClear = onQueryClear,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                SearchResultUiState.Loading -> MuseumLoadingView()

                is SearchResultUiState.Error -> MuseumErrorView(
                    message = state.message,
                    onRetry = onRetry,
                )

                is SearchResultUiState.Success -> if (state.isEmpty) {
                    MuseumEmptyView(
                        title = "검색 결과가 없습니다",
                        description = "다른 검색어를 쓰거나 필터를 줄여보세요.",
                        icon = MuseumIcons.Search,
                        actionLabel = "필터 조정".takeIf { state.appliedFilters.isNotEmpty() },
                        onAction = onFilterClick.takeIf { state.appliedFilters.isNotEmpty() },
                    )
                } else {
                    SearchResultContent(
                        state = state,
                        onSortClick = onSortClick,
                        onFilterClick = onFilterClick,
                        onFilterRemove = onFilterRemove,
                        onItemClick = onItemClick,
                        onItemSaveClick = onItemSaveClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        MuseumTabBar(
            selected = MuseumTab.Search,
            onSelect = onTabSelect,
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Composable
private fun SearchResultViewPreviewHost(state: SearchResultUiState) {
    EveryMuseumTheme {
        SearchResultView(
            state = state,
            onBackClick = {},
            onQueryChange = {},
            onSearch = {},
            onQueryClear = {},
            onSortClick = {},
            onFilterClick = {},
            onFilterRemove = {},
            onItemClick = {},
            onItemSaveClick = {},
            onRetry = {},
            onTabSelect = {},
        )
    }
}

@Preview(name = "검색 결과 · 성공 · Light", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewSuccessPreview() {
    SearchResultViewPreviewHost(SearchResultPreviewData.success)
}

@Preview(
    name = "검색 결과 · 성공 · Dark",
    device = PREVIEW_DEVICE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SearchResultViewSuccessDarkPreview() {
    SearchResultViewPreviewHost(SearchResultPreviewData.success)
}

@Preview(name = "검색 결과 · 필터 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewNoFilterPreview() {
    SearchResultViewPreviewHost(
        SearchResultPreviewData.success.copy(appliedFilters = emptyList()),
    )
}

@Preview(name = "검색 결과 · 로딩", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewLoadingPreview() {
    SearchResultViewPreviewHost(SearchResultUiState.Loading)
}

@Preview(name = "검색 결과 · 에러", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewErrorPreview() {
    SearchResultViewPreviewHost(SearchResultUiState.Error("검색에 실패했습니다."))
}

@Preview(name = "검색 결과 · 결과 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewEmptyPreview() {
    SearchResultViewPreviewHost(
        SearchResultPreviewData.success.copy(totalCount = 0, items = emptyList()),
    )
}

/** iPhone 16 시안(393×852)에 대응하는 안드로이드 기준 기기. */
private const val PREVIEW_DEVICE = "spec:width=393dp,height=852dp,dpi=440"

private object SearchResultPreviewData {

    val success = SearchResultUiState.Success(
        query = "백제",
        totalCount = 93,
        sort = "정확도순",
        appliedFilters = listOf(
            AppliedFilterUiModel(code = "PS08009", label = "금속"),
            AppliedFilterUiModel(code = "PS01001001", label = "국립중앙박물관"),
        ),
        items = listOf(
            ArtifactUiModel(
                id = "1",
                nameKr = "금동미륵보살반가사유상",
                museum = "국립중앙박물관",
                era = "삼국",
                designation = "국보",
                type = ArtifactType.Metal,
            ),
            ArtifactUiModel(
                id = "2",
                nameKr = "백자 달항아리",
                museum = "국립중앙박물관",
                era = "조선",
                designation = "보물",
                type = ArtifactType.Pottery,
            ),
            ArtifactUiModel(
                id = "3",
                nameKr = "두드린무늬항아리",
                museum = "국립중앙박물관 · 신수",
                era = "백제",
                type = ArtifactType.Pottery,
            ),
            ArtifactUiModel(
                id = "4",
                nameKr = "대당평일백제비 탑본",
                museum = "국립중앙박물관 · 본관",
                era = "통일신라",
                type = ArtifactType.Book,
            ),
            ArtifactUiModel(
                id = "5",
                nameKr = "사직단국왕친향도병풍",
                museum = "국립중앙박물관",
                era = "조선",
                type = ArtifactType.Painting,
            ),
            ArtifactUiModel(
                id = "6",
                nameKr = "석조여래좌상",
                museum = "국립경주박물관",
                era = "통일신라",
                type = ArtifactType.Stone,
            ),
        ),
    )
}
