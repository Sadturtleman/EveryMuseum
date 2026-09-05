package com.sadturtleman.androidsampleproject.common.presentation.helper

import com.sadturtleman.androidsampleproject.common.domain.helper.MessageHelper

/**
 * 북마크 토글 결과 알림.
 *
 * 홈 · 검색 결과 · 상세 · 보관함이 같은 문구와 되돌리기 동작을 써야 하므로 한 자리에 둔다.
 * (화면마다 두면 문구가 갈라진다)
 *
 * @param saved [com.sadturtleman.androidsampleproject.common.domain.saved.ToggleSavedRelicUseCase] 의 결과
 * @param onUndo 되돌리기. 같은 소장품으로 토글을 한 번 더 부르면 된다.
 */
fun MessageHelper.showSavedToggleResult(saved: Boolean, onUndo: () -> Unit) {
    showSnackbar(
        message = if (saved) "보관함에 저장했습니다" else "보관함에서 뺐습니다",
        actionLabel = "되돌리기",
        onAction = onUndo,
    )
}
