package com.sadturtleman.androidsampleproject.search.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumEmptyView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTab
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTabBar
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme
import com.sadturtleman.androidsampleproject.search.presentation.component.SearchTopBar

/**
 * 검색 화면 (Figma: 최종 → 02 · 검색).
 *
 * 상단 검색 바와 [SearchUiState] 분기를 담당하고, 본문은 [SearchContent] 에 맡긴다.
 * 아직 ViewModel 을 연결하지 않은 view-only 단계라 상태와 콜백을 모두 인자로 받는다.
 */
@Composable
fun SearchView(
    state: SearchUiState,
    onBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onQueryClear: () -> Unit,
    onRecentQueryClick: (String) -> Unit,
    onRecentQueryRemove: (String) -> Unit,
    onRecentQueryClearAll: () -> Unit,
    onIndexWordClick: (String) -> Unit,
    onCodeCategoryClick: (CodeCategoryUiModel) -> Unit,
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
            query = (state as? SearchUiState.Success)?.query.orEmpty(),
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
                SearchUiState.Loading -> MuseumLoadingView()

                is SearchUiState.Error -> MuseumErrorView(
                    message = state.message,
                    onRetry = onRetry,
                )

                is SearchUiState.Success -> if (state.isEverythingEmpty) {
                    MuseumEmptyView(
                        title = "둘러볼 항목이 없습니다",
                        description = "검색어를 입력해 소장품을 찾아보세요.",
                        icon = MuseumIcons.Search,
                    )
                } else {
                    SearchContent(
                        state = state,
                        onRecentQueryClick = onRecentQueryClick,
                        onRecentQueryRemove = onRecentQueryRemove,
                        onRecentQueryClearAll = onRecentQueryClearAll,
                        onIndexWordClick = onIndexWordClick,
                        onCodeCategoryClick = onCodeCategoryClick,
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

/** 세 블록이 모두 비면 화면에 그릴 것이 없으므로 빈 상태로 대체한다. */
private val SearchUiState.Success.isEverythingEmpty: Boolean
    get() = recentQueries.isEmpty() && popularIndexWords.isEmpty() && codeCategories.isEmpty()

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Composable
private fun SearchViewPreviewHost(state: SearchUiState) {
    EveryMuseumTheme {
        SearchView(
            state = state,
            onBackClick = {},
            onQueryChange = {},
            onSearch = {},
            onQueryClear = {},
            onRecentQueryClick = {},
            onRecentQueryRemove = {},
            onRecentQueryClearAll = {},
            onIndexWordClick = {},
            onCodeCategoryClick = {},
            onRetry = {},
            onTabSelect = {},
        )
    }
}

@Preview(name = "검색 · 성공 · Light", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchViewSuccessPreview() {
    SearchViewPreviewHost(SearchPreviewData.success)
}

@Preview(
    name = "검색 · 성공 · Dark",
    device = PREVIEW_DEVICE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SearchViewSuccessDarkPreview() {
    SearchViewPreviewHost(SearchPreviewData.success)
}

@Preview(name = "검색 · 질의 입력됨", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchViewTypingPreview() {
    SearchViewPreviewHost(SearchPreviewData.success.copy(query = "백제"))
}

@Preview(name = "검색 · 최근 검색어 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchViewNoRecentPreview() {
    SearchViewPreviewHost(SearchPreviewData.success.copy(recentQueries = emptyList()))
}

@Preview(name = "검색 · 로딩", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchViewLoadingPreview() {
    SearchViewPreviewHost(SearchUiState.Loading)
}

@Preview(name = "검색 · 에러", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchViewErrorPreview() {
    SearchViewPreviewHost(SearchUiState.Error("코드 목록을 불러오지 못했습니다."))
}

@Preview(name = "검색 · 전체 비어 있음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchViewEmptyPreview() {
    SearchViewPreviewHost(SearchUiState.Success())
}

/** iPhone 16 시안(393×852)에 대응하는 안드로이드 기준 기기. */
private const val PREVIEW_DEVICE = "spec:width=393dp,height=852dp,dpi=440"

private object SearchPreviewData {

    val success = SearchUiState.Success(
        query = "",
        recentQueries = listOf("백제", "금동미륵보살반가사유상", "김홍도", "달항아리"),
        popularIndexWords = listOf(
            "청자", "백자", "불상", "회화", "금관", "토기",
            "탑본", "병풍", "도자", "석기", "화폐",
        ),
        codeCategories = listOf(
            CodeCategoryUiModel(
                parentCode = "PS01",
                title = "소장기관별",
                summary = "PS01 · 국립 · 공립 · 사립 · 대학",
                icon = MuseumIcons.Museum,
            ),
            CodeCategoryUiModel(
                parentCode = "PS08",
                title = "재질별",
                summary = "PS08 · 금속 · 도자 · 종이 · 목재",
                icon = MuseumIcons.Layers,
            ),
            CodeCategoryUiModel(
                parentCode = "GL05",
                title = "출토지별",
                summary = "GL05 · 시 · 도 · 시군구",
                icon = MuseumIcons.MapPin,
            ),
            CodeCategoryUiModel(
                parentCode = "PS15",
                title = "크기별",
                summary = "PS15 · 5cm 이하 ~ 5m 이상",
                icon = MuseumIcons.Ruler,
            ),
        ),
    )
}
