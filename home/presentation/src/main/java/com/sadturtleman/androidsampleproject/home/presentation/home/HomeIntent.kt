package com.sadturtleman.androidsampleproject.home.presentation.home

import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviIntent

/** 홈 화면에서 올라오는 사용자 입력. `View → ViewModel` 방향으로만 흐른다. */
sealed interface HomeIntent : MviIntent {
    /** 상단 앱바 · 검색 바 탭. 둘 다 검색 화면으로 간다. */
    data object OpenSearch : HomeIntent
    data object OpenLibrary : HomeIntent
    data class SelectEra(val era: EraChipUiModel) : HomeIntent
    data object ClickHero : HomeIntent
    data object ClickSeeAll : HomeIntent
    data class ClickItem(val id: String) : HomeIntent
    data class ToggleSave(val id: String) : HomeIntent
    data object Retry : HomeIntent
}
