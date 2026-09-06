package com.sadturtleman.androidsampleproject.search.presentation.filter

import com.sadturtleman.androidsampleproject.common.presentation.mvi.ReducerEvent
import com.sadturtleman.androidsampleproject.search.presentation.result.AppliedFilterUiModel

/** 필터 시트 상태를 바꾸는 내부 이벤트. */
sealed interface FilterReducerEvent : ReducerEvent {
    /** 탭은 이미 알고 있으므로 로딩 중에도 함께 넘겨 시트 상단이 비지 않게 한다. */
    data class LoadStarted(
        val tabs: List<FilterTabUiModel>,
        val selectedTabCode: String?,
    ) : FilterReducerEvent

    data class LoadFailed(val message: String) : FilterReducerEvent

    /**
     * 옵션 조회 완료. 로딩 상태에는 선택 집합을 담을 자리가 없으므로
     * 지금까지 고른 코드를 함께 실어 보낸다(탭을 옮겨도 선택이 풀리지 않는다).
     */
    data class OptionsLoaded(
        val tabs: List<FilterTabUiModel>,
        val selectedTabCode: String,
        val options: List<FilterOptionUiModel>,
        val selectedFilters: List<AppliedFilterUiModel>,
        val resultCount: Int,
    ) : FilterReducerEvent

    /** 선택 변경. 토글 · 초기화 모두 확정된 목록을 그대로 싣는다. */
    data class SelectionChanged(val filters: List<AppliedFilterUiModel>) : FilterReducerEvent

    /**
     * 예상 건수 갱신. 선택이 바뀌면 화면은 즉시 반응하고 건수만 조회가 끝난 뒤 따라온다
     * (건수를 기다리느라 체크 표시가 늦게 켜지지 않도록).
     */
    data class ResultCountChanged(val count: Int) : FilterReducerEvent
}
