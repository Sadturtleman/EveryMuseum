package com.sadturtleman.androidsampleproject.search.presentation.search

import com.sadturtleman.androidsampleproject.common.presentation.mvi.ReducerEvent

/** 검색 화면 상태를 바꾸는 내부 이벤트. */
sealed interface SearchReducerEvent : ReducerEvent {
    data object LoadStarted : SearchReducerEvent
    data class LoadFailed(val message: String) : SearchReducerEvent
    data class Loaded(
        val popularIndexWords: List<String>,
        val codeCategories: List<CodeCategoryUiModel>,
    ) : SearchReducerEvent

    data class QueryChanged(val query: String) : SearchReducerEvent
    data class RecentQueriesChanged(val queries: List<String>) : SearchReducerEvent
}
