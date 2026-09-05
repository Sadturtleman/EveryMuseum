package com.sadturtleman.androidsampleproject.detail.presentation.detail

import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviIntent

/** 소장품 상세에서 올라오는 사용자 입력. */
sealed interface DetailIntent : MviIntent {
    data object Back : DetailIntent
    data object ToggleSave : DetailIntent
    data object Share : DetailIntent
    data class SelectImage(val image: DetailImageUiModel) : DetailIntent
    data class ClickRelated(val id: String) : DetailIntent
    data object ClickRelatedSeeAll : DetailIntent
    data object Retry : DetailIntent
}
