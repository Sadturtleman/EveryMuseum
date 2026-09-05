package com.sadturtleman.androidsampleproject.search.presentation.filter

import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviIntent

/** 필터 바텀시트에서 올라오는 사용자 입력. */
sealed interface FilterIntent : MviIntent {
    /**
     * 시트를 띄운 화면의 현재 질의어를 알려준다.
     * 예상 건수는 "질의어 + 필터" 로 세야 하는데 시트는 질의어를 스스로 알 수 없다.
     */
    data class SyncQuery(val query: String) : FilterIntent

    /**
     * "코드로 둘러보기" 로 들어온 경우 그 갈래 탭을 먼저 편다.
     * 탭 목록에 없는 코드면 무시하고 기본 탭을 유지한다.
     */
    data class SelectTabByCode(val parentCode: String) : FilterIntent
    data class SelectTab(val tab: FilterTabUiModel) : FilterIntent
    data class ToggleOption(val option: FilterOptionUiModel) : FilterIntent
    data object Reset : FilterIntent

    /**
     * 확인(결과 보기). 고른 코드를 가져가 목록을 다시 조회하고 시트를 닫는 것은
     * 필터 자신이 아니라 시트를 띄운 화면의 일이므로, 이 인텐트는 그쪽에서 가로챈다.
     * (`FilterUiState.appliedFilters()` 로 선택 결과를 읽는다)
     */
    data object Apply : FilterIntent
    data object Retry : FilterIntent
}
