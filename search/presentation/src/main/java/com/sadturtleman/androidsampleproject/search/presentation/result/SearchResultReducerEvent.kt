package com.sadturtleman.androidsampleproject.search.presentation.result

import com.sadturtleman.androidsampleproject.common.presentation.mvi.ReducerEvent

/** 검색 결과 상태를 바꾸는 내부 이벤트. */
sealed interface SearchResultReducerEvent : ReducerEvent {

    /**
     * 라우트 인자 도착. 검색 바 · 필터 시트의 시작 상태를 한 번에 만든다.
     */
    data class ArgsReceived(
        val query: String,
        val filterTabCode: String?,
    ) : SearchResultReducerEvent

    /** 검색 바 입력. 조회는 아직 나가지 않는다. */
    data class QueryChanged(val query: String) : SearchResultReducerEvent

    /** 조회에 실제로 적용된 필터. 시트에서 확인을 누르거나 칩을 뺀 시점에 확정된다. */
    data class FiltersChanged(val filters: List<AppliedFilterUiModel>) : SearchResultReducerEvent

    /** 전체 건수. 세는 동안에는 null 이 흘러 "N건" 자리가 비어 있는다. */
    data class TotalCountChanged(val totalCount: Int?) : SearchResultReducerEvent

    /** 필터 시트 표시 여부. 목록 조회와 무관하게 화면 레벨에서만 바뀐다. */
    data class FilterSheetVisibilityChanged(val visible: Boolean) : SearchResultReducerEvent
}
