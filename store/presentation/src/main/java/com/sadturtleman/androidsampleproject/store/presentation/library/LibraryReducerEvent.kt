package com.sadturtleman.androidsampleproject.store.presentation.library

import com.sadturtleman.androidsampleproject.common.presentation.mvi.ReducerEvent
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel

/** 보관함 상태를 바꾸는 내부 이벤트. */
sealed interface LibraryReducerEvent : ReducerEvent {
    data object LoadStarted : LibraryReducerEvent
    data class LoadFailed(val message: String) : LibraryReducerEvent

    /**
     * 저장 목록 변경. 보관함은 저장 flow 자체가 화면의 원본이라 갱신도 이 이벤트로 온다.
     * 로딩 상태에는 표시 방식을 담을 자리가 없어 [layout] 을 함께 싣는다.
     */
    data class Loaded(
        val items: List<ArtifactUiModel>,
        val sort: String,
        val layout: LibraryLayout,
    ) : LibraryReducerEvent

    data class LayoutChanged(val layout: LibraryLayout) : LibraryReducerEvent
}
