package com.sadturtleman.androidsampleproject.search.presentation.result

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.ArtifactType
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumEmptyView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumErrorView
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumLoadingView
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.preview.PREVIEW_DEVICE
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.EveryMuseumTheme
import com.sadturtleman.androidsampleproject.common.presentation.ui.theme.MuseumTheme
import com.sadturtleman.androidsampleproject.search.presentation.component.SearchTopBar
import com.sadturtleman.androidsampleproject.search.presentation.filter.FilterIntent
import com.sadturtleman.androidsampleproject.search.presentation.filter.FilterView
import com.sadturtleman.androidsampleproject.search.presentation.filter.FilterViewModel
import com.sadturtleman.androidsampleproject.search.presentation.filter.appliedFilters
import kotlinx.coroutines.flow.flowOf

/**
 * 검색 결과 라우트의 진입점.
 *
 * 시트를 띄울지 여부는 [SearchResultUiState.isFilterSheetVisible] 이 정하고,
 * 시트 안의 선택 상태는 같은 백스택 엔트리의 [FilterViewModel] 이 들고 있다.
 * 확인을 누른 순간에만 그 선택이 [SearchResultIntent.ApplyFilters] 로 결과 화면에 전달된다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    query: String,
    filterTabCode: String?,
    viewModel: SearchResultViewModel,
    filterViewModel: FilterViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by filterViewModel.uiState.collectAsStateWithLifecycle()

    // 라우트 인자는 생성자가 아니라 인텐트로 들어간다. ViewModel 이 두 번째부터는 무시한다.
    LaunchedEffect(query, filterTabCode) {
        viewModel.onIntent(SearchResultIntent.Load(query, filterTabCode))
    }

    SearchResultView(
        state = state,
        items = viewModel.items.collectAsLazyPagingItems(),
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )

    if (state.isFilterSheetVisible) {
        // 시트의 "N건 결과 보기" 는 질의어까지 반영한 건수라야 하는데
        // 필터 ViewModel 은 검색 바를 모른다. 여는 쪽이 자기 상태를 실어 보낸다.
        LaunchedEffect(state.query, state.filterTabCode) {
            filterViewModel.onIntent(FilterIntent.Open(state.query, state.filterTabCode))
        }

        // 시트 표면(라운드 · 핸들)은 FilterView 가 직접 그리므로 컨테이너는 비워 둔다.
        ModalBottomSheet(
            onDismissRequest = { viewModel.onIntent(SearchResultIntent.DismissFilter) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.Transparent,
            dragHandle = null,
        ) {
            FilterView(
                state = filterState,
                // 확인(Apply)만 여기서 가로챈다. 고른 코드를 읽어 결과 화면 인텐트로 옮기는
                // ViewModel 사이의 다리라 어느 한쪽 ViewModel 이 대신할 수 없다.
                // (시트를 닫는 것은 ApplyFilters 를 받은 결과 화면 ViewModel 이 한다)
                onIntent = { intent ->
                    if (intent is FilterIntent.Apply) {
                        viewModel.onIntent(
                            SearchResultIntent.ApplyFilters(filterState.appliedFilters())
                        )
                    } else {
                        filterViewModel.onIntent(intent)
                    }
                },
            )
        }
    }
}

/**
 * 검색 결과 화면 (Figma: 최종 → 03 · 검색 결과).
 *
 * 상단 검색 바와 목록 상태 분기를 담당하고, 본문은 [SearchResultContent] 에 맡긴다.
 *
 * 로딩 · 에러 · 결과 없음은 [SearchResultUiState] 가 아니라 Paging 의 첫 페이지 상태
 * (`loadState.refresh`)가 알려준다. 다음 페이지의 로딩 · 실패는 화면을 덮지 않고
 * 목록 바닥에서 처리된다.
 */
@Composable
internal fun SearchResultView(
    state: SearchResultUiState,
    items: LazyPagingItems<ArtifactUiModel>,
    onIntent: (SearchResultIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MuseumTheme.colors.bgCanvas),
    ) {
        SearchTopBar(
            query = state.query,
            onBackClick = { onIntent(SearchResultIntent.Back) },
            onQueryChange = { query -> onIntent(SearchResultIntent.ChangeQuery(query)) },
            onSearch = { onIntent(SearchResultIntent.Submit) },
            onClear = { onIntent(SearchResultIntent.ClearQuery) },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val refresh = items.loadState.refresh
            when {
                refresh is LoadState.Loading -> MuseumLoadingView()

                refresh is LoadState.Error -> MuseumErrorView(
                    message = refresh.error.message ?: "검색 결과를 불러오지 못했습니다.",
                    onRetry = {
                        items.retry()
                        onIntent(SearchResultIntent.Retry)
                    },
                )

                items.itemCount == 0 -> MuseumEmptyView(
                    title = "검색 결과가 없습니다",
                    description = "다른 검색어를 쓰거나 필터를 줄여보세요.",
                    icon = MuseumIcons.Search,
                    actionLabel = "필터 조정".takeIf { state.appliedFilters.isNotEmpty() },
                    onAction = { onIntent(SearchResultIntent.OpenFilter) }
                        .takeIf { state.appliedFilters.isNotEmpty() },
                )

                else -> SearchResultContent(
                    state = state,
                    items = items,
                    onIntent = onIntent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Composable
private fun SearchResultViewPreviewHost(
    state: SearchResultUiState = SearchResultPreviewData.state,
    items: List<ArtifactUiModel> = SearchResultPreviewData.items,
    loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = true),
) {
    EveryMuseumTheme {
        SearchResultView(
            state = state,
            items = flowOf(
                PagingData.from(
                    data = items,
                    sourceLoadStates = LoadStates(
                        refresh = loadState,
                        prepend = LoadState.NotLoading(endOfPaginationReached = true),
                        append = LoadState.NotLoading(endOfPaginationReached = true),
                    ),
                )
            ).collectAsLazyPagingItems(),
            onIntent = {},
        )
    }
}

@Preview(name = "검색 결과 · 성공 · Light", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewSuccessPreview() {
    SearchResultViewPreviewHost()
}

@Preview(
    name = "검색 결과 · 성공 · Dark",
    device = PREVIEW_DEVICE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SearchResultViewSuccessDarkPreview() {
    SearchResultViewPreviewHost()
}

@Preview(name = "검색 결과 · 필터 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewNoFilterPreview() {
    SearchResultViewPreviewHost(
        state = SearchResultPreviewData.state.copy(appliedFilters = emptyList()),
    )
}

@Preview(name = "검색 결과 · 건수 조회 중", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewCountingPreview() {
    SearchResultViewPreviewHost(
        state = SearchResultPreviewData.state.copy(totalCount = null),
    )
}

@Preview(name = "검색 결과 · 로딩", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewLoadingPreview() {
    SearchResultViewPreviewHost(items = emptyList(), loadState = LoadState.Loading)
}

@Preview(name = "검색 결과 · 에러", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewErrorPreview() {
    SearchResultViewPreviewHost(
        items = emptyList(),
        loadState = LoadState.Error(IllegalStateException("검색에 실패했습니다.")),
    )
}

@Preview(name = "검색 결과 · 결과 없음", device = PREVIEW_DEVICE, showBackground = true)
@Composable
private fun SearchResultViewEmptyPreview() {
    SearchResultViewPreviewHost(
        state = SearchResultPreviewData.state.copy(totalCount = 0),
        items = emptyList(),
    )
}

private object SearchResultPreviewData {

    val state = SearchResultUiState(
        query = "백제",
        totalCount = 93,
        appliedFilters = listOf(
            AppliedFilterUiModel(code = "PS08001", label = "금속"),
            AppliedFilterUiModel(code = "PS01001001", label = "국립중앙박물관"),
        ),
    )

    val items = listOf(
        ArtifactUiModel(
            id = "1",
            nameKr = "금동미륵보살반가사유상",
            museum = "국립중앙박물관 · 본관",
            era = "삼국",
            designation = "국보",
            type = ArtifactType.Metal,
        ),
        ArtifactUiModel(
            id = "2",
            nameKr = "백제금동대향로",
            museum = "국립부여박물관",
            era = "백제",
            designation = "국보",
            type = ArtifactType.Metal,
        ),
        ArtifactUiModel(
            id = "3",
            nameKr = "산수무늬 벽돌",
            museum = "국립중앙박물관 · 본관",
            era = "백제",
            type = ArtifactType.Pottery,
        ),
    )
}
