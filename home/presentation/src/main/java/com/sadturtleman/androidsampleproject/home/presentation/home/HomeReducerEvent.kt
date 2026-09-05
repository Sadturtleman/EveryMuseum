package com.sadturtleman.androidsampleproject.home.presentation.home

import com.sadturtleman.androidsampleproject.common.presentation.mvi.ReducerEvent
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel

/** 홈 상태를 바꾸는 내부 이벤트. ViewModel 안에서만 dispatch 된다. */
sealed interface HomeReducerEvent : ReducerEvent {
    data object LoadStarted : HomeReducerEvent
    data class LoadFailed(val message: String) : HomeReducerEvent
    /**
     * 조회 완료. 로딩 상태에는 선택된 시대를 담을 자리가 없으므로
     * 요청 시점의 값을 이벤트에 함께 실어 성공 상태를 한 번에 만든다.
     */
    data class Loaded(
        val hero: HomeHeroUiModel?,
        val eras: List<EraChipUiModel>,
        val selectedEraCode: String?,
        val items: List<ArtifactUiModel>,
    ) : HomeReducerEvent

    /** 시대 칩 선택. 목록 재조회는 [Loaded] 로 따로 온다. */
    data class EraSelected(val code: String?) : HomeReducerEvent

    /** 보관함 저장 id 집합 변경. 노출 중인 카드들의 북마크 상태에 접어 반영한다. */
    data class SavedIdsChanged(val ids: Set<String>) : HomeReducerEvent
}
