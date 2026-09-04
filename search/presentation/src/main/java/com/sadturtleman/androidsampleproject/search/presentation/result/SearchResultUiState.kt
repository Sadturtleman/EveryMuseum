package com.sadturtleman.androidsampleproject.search.presentation.result

import androidx.compose.runtime.Immutable
import com.sadturtleman.androidsampleproject.common.presentation.ui.model.ArtifactUiModel

/**
 * 검색 결과 화면 상태 (Figma: 최종 → 03 · 검색 결과).
 *
 * 데이터 출처는 `GET /openapi/relic/list` 이며, 적용된 필터의 코드가
 * materialCode · museumCode · nationalityCode 등의 파라미터로 전달된다.
 */
sealed interface SearchResultUiState {

    /** 질의 실행 중 */
    data object Loading : SearchResultUiState

    /** 조회 실패 */
    data class Error(val message: String) : SearchResultUiState

    /**
     * 조회 성공. [items] 가 비면 결과 없음 화면을 그린다.
     *
     * @param totalCount 전체 건수. 페이지네이션 전 총합이다.
     * @param sort 현재 정렬 라벨 (예: "정확도순")
     * @param appliedFilters 적용된 필터 칩. 비면 필터 줄을 그리지 않는다.
     */
    data class Success(
        val query: String,
        val totalCount: Int,
        val sort: String,
        val appliedFilters: List<AppliedFilterUiModel>,
        val items: List<ArtifactUiModel>,
    ) : SearchResultUiState {

        val isEmpty: Boolean get() = items.isEmpty()
    }
}

/**
 * 적용된 필터 칩 하나.
 *
 * @param code `view_relic_list` 로 넘어가는 코드값
 * @param label 사용자에게 보이는 이름 (nameKr)
 */
@Immutable
data class AppliedFilterUiModel(
    val code: String,
    val label: String,
)
