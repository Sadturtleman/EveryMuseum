package com.sadturtleman.androidsampleproject.store.presentation.library

import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel

/**
 * 보관함 상태 (Figma: 최종 → 06 · 보관함).
 *
 * 로컬에 저장한 id 목록으로 `GET /openapi/relic/list` 를 조회해 채운다.
 */
sealed interface LibraryUiState {

    /** 저장 목록 조회 중 */
    data object Loading : LibraryUiState

    /** 조회 실패 */
    data class Error(val message: String) : LibraryUiState

    /**
     * 조회 성공. [items] 가 비면 빈 보관함 화면을 그린다.
     *
     * @param sort 현재 정렬 라벨 (예: "최근 저장순")
     * @param layout 목록 · 그리드 전환 상태. 상단 아이콘 버튼으로 토글한다.
     */
    data class Success(
        val items: List<ArtifactUiModel>,
        val sort: String,
        val layout: LibraryLayout = LibraryLayout.List,
    ) : LibraryUiState {

        val isEmpty: Boolean get() = items.isEmpty()
    }
}

/** 보관함 목록 표시 방식. */
enum class LibraryLayout {
    /** 밀도 높은 리스트 (시안 기본값) */
    List,

    /** 2열 그리드 */
    Grid,
}
