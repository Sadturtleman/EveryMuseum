package com.sadturtleman.androidsampleproject.search.presentation.result

import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel
import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviIntent

/** 검색 결과 화면에서 올라오는 사용자 입력. */
sealed interface SearchResultIntent : MviIntent {
    data object Back : SearchResultIntent
    data class ChangeQuery(val query: String) : SearchResultIntent

    /** 검색 바에서 다시 검색. 같은 화면에서 목록만 갈아끼운다. */
    data object Submit : SearchResultIntent
    data object ClearQuery : SearchResultIntent
    data class RemoveFilter(val filter: AppliedFilterUiModel) : SearchResultIntent

    /** 필터 버튼. 시트를 연다. */
    data object OpenFilter : SearchResultIntent

    /** 시트 바깥 탭 · 드래그로 닫힘. */
    data object DismissFilter : SearchResultIntent

    /** 필터 시트에서 확인을 누른 결과. 목록을 다시 조회하고 시트를 닫는다. */
    data class ApplyFilters(val filters: List<AppliedFilterUiModel>) : SearchResultIntent
    data class ClickItem(val id: String) : SearchResultIntent

    /**
     * 북마크 토글.
     *
     * 목록이 Paging 으로 흘러 ViewModel 이 id 로 카드를 되찾을 수 없으므로,
     * 저장본에 담을 표시값을 카드째 실어 올린다.
     */
    data class ToggleSave(val item: ArtifactUiModel) : SearchResultIntent

    /** 조회 실패 후 다시 시도. 목록은 Paging 이 재시도하고 건수는 다시 센다. */
    data object Retry : SearchResultIntent
}
