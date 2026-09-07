package com.sadturtleman.androidsampleproject.search.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumEmptyView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.preview.PREVIEW_DEVICE
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme
import com.sadturtleman.androidsampleproject.search.presentation.component.SearchTopBar
import com.sadturtleman.androidsampleproject.tti.domain.TtiTimeline
import com.sadturtleman.androidsampleproject.tti.presentation.TtiDrawnEffect
import com.sadturtleman.androidsampleproject.tti.presentation.TtiEmptySpanEffect
import com.sadturtleman.androidsampleproject.tti.presentation.TtiSpanEffect

/**
 * 검색 라우트의 진입점.
 *
 * 상태 [SearchViewModel.uiState] 를 읽어 내려보내고 인텐트를 [SearchViewModel.onIntent] 로
 * 올려보내는 두 배선이 전부다.
 */
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // TTI 계측. 최근 검색어 · 색인어 · 코드 목록뿐이라 뒤늦게 채워지는 큰 덩어리가 없다.
    TtiSpanEffect(TtiTimeline.BACKEND, running = state is SearchUiState.Loading)
    TtiDrawnEffect(ready = state is SearchUiState.Success)
    TtiEmptySpanEffect(TtiTimeline.BIG_PART_LOADING)

    SearchView(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/**
 * 검색 화면 (Figma: 최종 → 02 · 검색).
 *
 * 상단 검색 바와 [SearchUiState] 분기를 담당하고, 본문은 [SearchContent] 에 맡긴다.
 * 입력은 [SearchUiState] 하나, 출력은 [SearchIntent] 하나다.
 */
@Composable
internal fun SearchView(
    state: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuseumTheme.colors.bgCanvas),
    ) {
        SearchTopBar(
            query = (state as? SearchUiState.Success)?.query.orEmpty(),
            onBackClick = { onIntent(SearchIntent.Back) },
            onQueryChange = { query -> onIntent(SearchIntent.ChangeQuery(query)) },
            onSearch = { onIntent(SearchIntent.Submit) },
            onClear = { onIntent(SearchIntent.ClearQuery) },
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
                    onRetry = { onIntent(SearchIntent.Retry) },
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
                        onIntent = onIntent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
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
        SearchView(state = state, onIntent = {})
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
