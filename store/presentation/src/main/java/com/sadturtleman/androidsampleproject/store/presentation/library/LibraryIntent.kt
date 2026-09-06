package com.sadturtleman.androidsampleproject.store.presentation.library

import com.sadturtleman.androidsampleproject.common.presentation.mvi.MviIntent

/** 보관함 화면에서 올라오는 사용자 입력. */
sealed interface LibraryIntent : MviIntent {
    /** 목록 ↔ 그리드 전환. */
    data object ToggleLayout : LibraryIntent

    /** 정렬 라벨을 다음 값으로 넘긴다. (시안에 정렬 시트가 없어 토글로 둔다) */
    data object ToggleSort : LibraryIntent
    data class ClickItem(val id: String) : LibraryIntent

    /** 카드의 북마크 해제. 보관함에서는 목록에서 빠지는 동작이 된다. */
    data class ToggleSave(val id: String) : LibraryIntent

    /** 빈 보관함의 "소장품 둘러보기". */
    data object Explore : LibraryIntent
    data object Retry : LibraryIntent
}
