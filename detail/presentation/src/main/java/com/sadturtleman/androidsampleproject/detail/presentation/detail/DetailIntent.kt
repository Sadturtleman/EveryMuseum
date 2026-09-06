package com.sadturtleman.androidsampleproject.detail.presentation.detail

import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviIntent

/** 소장품 상세에서 올라오는 사용자 입력. */
sealed interface DetailIntent : MviIntent {
    /**
     * 라우트 인자 전달. 화면이 진입할 때 한 번 보낸다.
     *
     * 생성자로 받지 않는 이유는 그러려면 assisted injection 과 팩토리가 필요한데,
     * MVI 에서는 밖에서 들어오는 값도 인텐트로 표현하는 편이 일관되기 때문이다.
     */
    data class Load(val id: String) : DetailIntent

    data object Back : DetailIntent
    data object ToggleSave : DetailIntent
    data object Share : DetailIntent
    data class SelectImage(val image: DetailImageUiModel) : DetailIntent
    data class ClickRelated(val id: String) : DetailIntent
    data object ClickRelatedSeeAll : DetailIntent
    data object Retry : DetailIntent
}
