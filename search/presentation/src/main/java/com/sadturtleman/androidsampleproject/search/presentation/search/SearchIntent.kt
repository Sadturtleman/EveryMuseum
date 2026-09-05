package com.sadturtleman.androidsampleproject.search.presentation.search

import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviIntent
import com.sadturtleman.androidsampleproject.common.presentation.ui.component.MuseumTab

/** 검색 화면에서 올라오는 사용자 입력. */
sealed interface SearchIntent : MviIntent {
    data object Back : SearchIntent
    data class ChangeQuery(val query: String) : SearchIntent

    /** 키보드 검색 키. 현재 입력값으로 검색 결과 화면에 진입한다. */
    data object Submit : SearchIntent
    data object ClearQuery : SearchIntent
    data class ClickRecentQuery(val query: String) : SearchIntent
    data class RemoveRecentQuery(val query: String) : SearchIntent
    data object ClearRecentQueries : SearchIntent
    data class ClickIndexWord(val word: String) : SearchIntent
    data class ClickCodeCategory(val category: CodeCategoryUiModel) : SearchIntent
    data object Retry : SearchIntent
}
