package com.sadturtleman.androidsampleproject.detail.presentation.detail

import com.sadturtleman.androidsampleproject.common.presentation.mvi.ReducerEvent

/** 상세 상태를 바꾸는 내부 이벤트. */
sealed interface DetailReducerEvent : ReducerEvent {
    data object LoadStarted : DetailReducerEvent
    data class LoadFailed(val message: String) : DetailReducerEvent

    /** 조회 성공. 화면에 그릴 값이 모두 채워진 상태를 그대로 싣는다. */
    data class Loaded(val success: DetailUiState.Success) : DetailReducerEvent

    data class SavedChanged(val saved: Boolean) : DetailReducerEvent
    data class ImageSelected(val index: Int) : DetailReducerEvent
}
