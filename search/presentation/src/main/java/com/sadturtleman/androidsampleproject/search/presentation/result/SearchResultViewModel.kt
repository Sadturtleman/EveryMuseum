package com.sadturtleman.androidsampleproject.search.presentation.result

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.sadturtleman.androidsampleproject.common.domain.helper.MessageHelper
import com.sadturtleman.androidsampleproject.common.navigation.NavigationHelper
import com.sadturtleman.androidsampleproject.common.domain.saved.GetSavedRelicIdsUseCase
import com.sadturtleman.androidsampleproject.common.domain.saved.ToggleSavedRelicUseCase
import com.sadturtleman.androidsampleproject.common.presentation.helper.showSavedToggleResult
import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviViewModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.toArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.toSavedRelicVO
import com.sadturtleman.androidsampleproject.detail.navigation.DetailPage
import com.sadturtleman.androidsampleproject.search.domain.CountRelicsUseCase
import com.sadturtleman.androidsampleproject.search.domain.SearchRelicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 검색 결과 화면 ViewModel (Figma: 최종 → 03 · 검색 결과).
 *
 * 네비게이션 인자는 화면이 [SearchResultIntent.Load] 로 넣어 준다.
 *
 * 결과가 수십만 건이라 목록은 [uiState] 가 아니라 [items] 로 나간다.
 * 상태에 List 를 담으면 페이지를 이어 붙일 때마다 화면 전체 상태를 새로 만들어야 하고,
 * 로딩 · 에러도 페이지마다 따로 필요해진다 — 그건 Paging 의 LoadState 가 이미 하는 일이다.
 * 그래서 상태에는 목록을 둘러싼 값(검색 바 · 건수 · 필터 칩 · 시트)만 남는다.
 *
 * 사용자가 입력 중인 질의([SearchResultUiState.query])와 실제로 조회에 쓰인 조건([executedSearch])을
 * 나눠 둔다. 타이핑할 때마다 조회가 나가면 안 되기 때문이다.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchRelics: SearchRelicsUseCase,
    private val countRelics: CountRelicsUseCase,
    getSavedRelicIds: GetSavedRelicIdsUseCase,
    private val toggleSavedRelic: ToggleSavedRelicUseCase,
    private val navigationHelper: NavigationHelper,
    private val messageHelper: MessageHelper,
) : MviViewModel<SearchResultIntent, SearchResultUiState, SearchResultReducerEvent>(
    SearchResultUiState(),
) {

    /**
     * 실제로 조회에 쓰인 조건. 여기가 바뀔 때만 새 Pager 가 만들어진다.
     * [SearchResultIntent.Load] 가 오기 전에는 null 이라 조회가 나가지 않는다
     * (빈 조건으로 한 번 조회한 뒤 진짜 조건으로 또 조회하는 낭비를 막는다).
     */
    private val executedSearch = MutableStateFlow<ExecutedSearch?>(null)

    /** 저장 목록은 화면 밖(상세 · 보관함)에서도 바뀌므로 계속 지켜본다. */
    private val savedIds = getSavedRelicIds()
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private var countJob: Job? = null

    /**
     * 검색 결과 목록.
     *
     * [cachedIn] 을 저장 목록과 합치기 **전에** 둔다. 북마크를 누를 때마다 뒤의 combine 이 다시 흐르는데,
     * 그 지점이 캐시 뒤라면 이미 받아 둔 페이지를 네트워크에서 다시 받아오게 된다.
     */
    val items: Flow<PagingData<ArtifactUiModel>> = executedSearch
        .filterNotNull()
        .flatMapLatest { search -> searchRelics(search.query, search.filterCodes) }
        .map { paging -> paging.map { relic -> relic.toArtifactUiModel() } }
        .cachedIn(viewModelScope)
        .combine(savedIds) { paging, ids ->
            paging.map { card -> card.copy(saved = card.id in ids) }
        }

    override fun onIntent(intent: SearchResultIntent) {
        when (intent) {
            is SearchResultIntent.Load -> load(intent.query, intent.filterTabCode)

            SearchResultIntent.Back -> navigationHelper.navigateToBack()

            is SearchResultIntent.ChangeQuery ->
                dispatch(SearchResultReducerEvent.QueryChanged(intent.query))

            SearchResultIntent.Submit -> executeSearch()

            SearchResultIntent.ClearQuery ->
                dispatch(SearchResultReducerEvent.QueryChanged(""))

            SearchResultIntent.OpenFilter ->
                dispatch(SearchResultReducerEvent.FilterSheetVisibilityChanged(visible = true))

            SearchResultIntent.DismissFilter ->
                dispatch(SearchResultReducerEvent.FilterSheetVisibilityChanged(visible = false))

            is SearchResultIntent.RemoveFilter -> {
                dispatch(
                    SearchResultReducerEvent.FiltersChanged(
                        currentState.appliedFilters - intent.filter
                    )
                )
                executeSearch()
            }

            is SearchResultIntent.ApplyFilters -> {
                dispatch(SearchResultReducerEvent.FiltersChanged(intent.filters))
                dispatch(SearchResultReducerEvent.FilterSheetVisibilityChanged(visible = false))
                executeSearch()
            }

            is SearchResultIntent.ClickItem ->
                navigationHelper.navigateTo(DetailPage.Args(id = intent.id))

            is SearchResultIntent.ToggleSave -> {
                val relic = intent.item.toSavedRelicVO()
                viewModelScope.launch {
                    val saved = toggleSavedRelic(relic)
                    messageHelper.showSavedToggleResult(saved) {
                        viewModelScope.launch { toggleSavedRelic(relic) }
                    }
                }
            }

            SearchResultIntent.Retry -> refreshTotalCount()
        }
    }

    override fun reduce(
        state: SearchResultUiState,
        event: SearchResultReducerEvent,
    ): SearchResultUiState = when (event) {
        is SearchResultReducerEvent.ArgsReceived -> state.copy(
            query = event.query,
            filterTabCode = event.filterTabCode,
            // 코드로 둘러보기로 들어왔으면 그 갈래 시트를 펴고 시작한다.
            isFilterSheetVisible = event.filterTabCode != null,
        )

        is SearchResultReducerEvent.QueryChanged -> state.copy(query = event.query)

        is SearchResultReducerEvent.FiltersChanged -> state.copy(appliedFilters = event.filters)

        is SearchResultReducerEvent.TotalCountChanged -> state.copy(totalCount = event.totalCount)

        is SearchResultReducerEvent.FilterSheetVisibilityChanged ->
            state.copy(isFilterSheetVisible = event.visible)
    }

    /**
     * 인자를 받아 첫 조회를 시작한다. 두 번째부터는 무시한다
     * (백스택 엔트리마다 ViewModel 이 따로 살아 있고, 회전으로 다시 와도 재조회하지 않아야 한다).
     */
    private fun load(query: String, filterTabCode: String?) {
        if (executedSearch.value != null) return
        dispatch(SearchResultReducerEvent.ArgsReceived(query, filterTabCode))
        executeSearch()
    }

    /**
     * 지금 화면의 질의 · 필터로 조회 조건을 확정한다.
     * 조건이 그대로면 [MutableStateFlow] 가 같은 값을 걸러 내 목록이 다시 흐르지 않는다.
     */
    private fun executeSearch() {
        executedSearch.value = ExecutedSearch(
            query = currentState.query,
            filterCodes = currentState.appliedFilters.map { it.code },
        )
        refreshTotalCount()
    }

    /**
     * 전체 건수를 다시 센다. PagingData 는 건수를 싣지 않아 별도 조회다(행 없이 totalCount 만 읽는다).
     * 실패해도 목록은 그대로 둔다 — "N건" 자리만 비고 결과는 보인다.
     */
    private fun refreshTotalCount() {
        val search = executedSearch.value ?: return
        countJob?.cancel()
        dispatch(SearchResultReducerEvent.TotalCountChanged(null))
        countJob = viewModelScope.launch {
            runCatching { countRelics(search.query, search.filterCodes) }
                .onSuccess { dispatch(SearchResultReducerEvent.TotalCountChanged(it)) }
        }
    }
}

/** 조회에 실제로 쓰인 조건 한 벌. */
private data class ExecutedSearch(
    val query: String,
    val filterCodes: List<String> = emptyList(),
)
