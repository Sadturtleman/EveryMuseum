package com.sadturtleman.androidsampleproject.search.presentation.search

import androidx.lifecycle.viewModelScope
import com.sadturtleman.androidsampleproject.common.domain.code.GetCodesUseCase
import com.sadturtleman.androidsampleproject.common.navigation.NavigationHelper
import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviViewModel
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumIcons
import com.sadturtleman.androidsampleproject.logging.domain.BizEvent
import com.sadturtleman.androidsampleproject.logging.domain.BizLogger
import com.sadturtleman.androidsampleproject.search.navigation.SearchPage
import com.sadturtleman.androidsampleproject.search.navigation.SearchResultPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 검색 화면 ViewModel (Figma: 최종 → 02 · 검색).
 *
 * 이 화면은 질의를 "받기만" 한다. 실제 조회는 검색 결과 화면(SearchResultScreen)이 맡으므로
 * 검색 실행 인텐트는 결과 화면으로의 이동으로 끝난다.
 *
 * TODO(api): 인기 색인어는 아직 고정 목록이고, 최근 검색어는 메모리에만 남아 프로세스가 죽으면 사라진다
 *  (DataStore 로 옮길 자리다).
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val navigationHelper: NavigationHelper,
    private val bizLogger: BizLogger,
) : MviViewModel<SearchIntent, SearchUiState, SearchReducerEvent>(SearchUiState.Loading) {

    private val recentQueries = ArrayDeque<String>()

    init {
        load()
    }

    override fun onIntent(intent: SearchIntent) {
        when (intent) {
            SearchIntent.Back -> navigationHelper.navigateToBack()
            is SearchIntent.ChangeQuery -> dispatch(SearchReducerEvent.QueryChanged(intent.query))
            SearchIntent.Submit -> submit(currentQuery())
            SearchIntent.ClearQuery -> dispatch(SearchReducerEvent.QueryChanged(""))
            is SearchIntent.ClickRecentQuery -> submit(intent.query)
            is SearchIntent.RemoveRecentQuery -> removeRecentQuery(intent.query)
            SearchIntent.ClearRecentQueries -> clearRecentQueries()
            is SearchIntent.ClickIndexWord -> submit(intent.word)
            is SearchIntent.ClickCodeCategory -> openCodeCategory(intent.category)
            SearchIntent.Retry -> load()
        }
    }

    override fun reduce(
        state: SearchUiState,
        event: SearchReducerEvent,
    ): SearchUiState = when (event) {
        SearchReducerEvent.LoadStarted -> SearchUiState.Loading

        is SearchReducerEvent.LoadFailed -> SearchUiState.Error(event.message)

        is SearchReducerEvent.Loaded -> SearchUiState.Success(
            // 로딩 전에 이미 입력한 질의는 그대로 살린다.
            query = (state as? SearchUiState.Success)?.query.orEmpty(),
            recentQueries = recentQueries.toList(),
            popularIndexWords = event.popularIndexWords,
            codeCategories = event.codeCategories,
        )

        is SearchReducerEvent.QueryChanged -> state.mapSuccess { it.copy(query = event.query) }

        is SearchReducerEvent.RecentQueriesChanged ->
            state.mapSuccess { it.copy(recentQueries = event.queries) }
    }

    private fun load() {
        dispatch(SearchReducerEvent.LoadStarted)
        viewModelScope.launch {
            runCatching { POPULAR_INDEX_WORDS to CODE_CATEGORIES }
                .onSuccess { (words, categories) ->
                    dispatch(SearchReducerEvent.Loaded(words, categories))
                }
                .onFailure { throwable ->
                    dispatch(
                        SearchReducerEvent.LoadFailed(
                            throwable.message ?: "코드 목록을 불러오지 못했습니다."
                        )
                    )
                }
        }
    }

    /**
     * 질의를 확정하고 결과 화면으로 넘긴다.
     * 공백만 입력한 경우는 최근 검색어를 더럽히지 않도록 무시한다.
     */
    private fun submit(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        dispatch(SearchReducerEvent.QueryChanged(trimmed))
        // 최근 검색어 · 인기어를 눌러 들어온 것도 같은 자리를 지나므로 여기 한 번이면 된다.
        bizLogger.record(SearchPage.PATH, BizEvent.SearchSubmit(query = trimmed))
        addRecentQuery(trimmed)
        navigationHelper.navigateTo(SearchResultPage.Args(query = trimmed))
    }

    /** 같은 검색어는 맨 앞으로 끌어올리고, 목록은 [MAX_RECENT_QUERIES] 개까지만 남긴다. */
    private fun addRecentQuery(query: String) {
        recentQueries.remove(query)
        recentQueries.addFirst(query)
        while (recentQueries.size > MAX_RECENT_QUERIES) recentQueries.removeLast()
        dispatch(SearchReducerEvent.RecentQueriesChanged(recentQueries.toList()))
    }

    private fun removeRecentQuery(query: String) {
        if (!recentQueries.remove(query)) return
        dispatch(SearchReducerEvent.RecentQueriesChanged(recentQueries.toList()))
    }

    private fun clearRecentQueries() {
        if (recentQueries.isEmpty()) return
        bizLogger.record(SearchPage.PATH, BizEvent.SearchRecentClear)
        recentQueries.clear()
        dispatch(SearchReducerEvent.RecentQueriesChanged(emptyList()))
    }

    /**
     * 코드로 둘러보기 진입.
     *
     * 카테고리는 검색어가 아니라 **필터 갈래** 다. 제목("재질별")을 질의로 넘기면
     * `name=재질별` 로 검색되어 결과가 0건이 되므로, 상위 코드를 실어 보내
     * 결과 화면이 그 갈래의 필터 시트를 펴게 한다.
     */
    private fun openCodeCategory(category: CodeCategoryUiModel) {
        navigationHelper.navigateTo(
            SearchResultPage.Args(filterTabCode = category.parentCode)
        )
    }

    private fun currentQuery(): String = (currentState as? SearchUiState.Success)?.query.orEmpty()

    private companion object {
        const val MAX_RECENT_QUERIES = 10

        val POPULAR_INDEX_WORDS = listOf(
            "청자", "백자", "불상", "회화", "금관", "토기",
            "탑본", "병풍", "도자", "석기", "화폐",
        )

        /** 필터 시트의 탭과 짝이 맞아야 한다 — 여기서 넘긴 코드로 그 탭이 펼쳐진다. */
        val CODE_CATEGORIES = listOf(
            CodeCategoryUiModel(
                parentCode = GetCodesUseCase.ParentCode.NATIONALITY_KOREA,
                title = "시대별",
                summary = "구석기 · 삼국 · 고려 · 조선 · 근현대",
                icon = MuseumIcons.Layers,
            ),
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
                parentCode = "PS15",
                title = "크기별",
                summary = "PS15 · 5cm 이하 ~ 5m 이상",
                icon = MuseumIcons.Ruler,
            ),
        )
    }
}

/** 성공 상태에서만 변형을 적용한다. 로딩 · 에러 중 들어온 이벤트는 무시된다. */
private inline fun SearchUiState.mapSuccess(
    transform: (SearchUiState.Success) -> SearchUiState,
): SearchUiState = if (this is SearchUiState.Success) transform(this) else this
