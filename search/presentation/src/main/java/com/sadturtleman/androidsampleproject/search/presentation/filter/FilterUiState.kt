package com.sadturtleman.androidsampleproject.search.presentation.filter

import androidx.compose.runtime.Immutable
import com.sadturtleman.androidsampleproject.common.presentation.mvi.UiState
import com.sadturtleman.androidsampleproject.search.presentation.result.AppliedFilterUiModel

/**
 * 필터 바텀시트 상태 (Figma: 최종 → 04 · 필터).
 *
 * 분류 코드 탭을 고르면 `GET /openapi/code?parentCode=<탭의 코드>` 로 옵션을 다시 받아오고,
 * 선택된 코드는 `view_relic_list` 의 해당 파라미터로 전달된다.
 */
sealed interface FilterUiState : UiState {

    /** 옵션 목록을 불러오는 중. 탭은 이미 알고 있으므로 함께 넘겨 유지한다. */
    data class Loading(
        val tabs: List<FilterTabUiModel> = emptyList(),
        val selectedTabCode: String? = null,
    ) : FilterUiState

    /** 조회 실패 */
    data class Error(val message: String) : FilterUiState

    /**
     * 조회 성공.
     *
     * @param options 현재 탭의 코드 옵션. 비면 빈 상태를 그린다.
     * @param selectedFilters 고른 옵션. 라벨까지 함께 들고 있어야 시트를 닫은 뒤에도
     *  검색 결과 화면이 칩을 그릴 수 있다(탭을 옮기면 그 코드의 옵션 목록은 사라진다).
     * @param resultCount 현재 선택으로 예상되는 결과 건수. 확인 버튼 라벨에 쓴다.
     */
    data class Success(
        val tabs: List<FilterTabUiModel>,
        val selectedTabCode: String,
        val options: List<FilterOptionUiModel>,
        val selectedFilters: List<AppliedFilterUiModel>,
        val resultCount: Int,
    ) : FilterUiState {

        val isEmpty: Boolean get() = options.isEmpty()

        /** 옵션 행의 체크 표시에 쓴다. 원본은 [selectedFilters] 하나다. */
        val selectedCodes: Set<String> get() = selectedFilters.mapTo(mutableSetOf()) { it.code }
    }
}

/**
 * 분류 코드 탭 하나.
 *
 * @param parentCode `view_code_list` 의 parentCode (예: PS06)
 */
@Immutable
data class FilterTabUiModel(
    val parentCode: String,
    val label: String,
)

/**
 * 코드 옵션 한 줄.
 *
 * @param code view_relic_list 파라미터로 전달되는 코드 (예: PS06001009)
 * @param label nameKr (예: 한국 · 백제)
 */
@Immutable
data class FilterOptionUiModel(
    val code: String,
    val label: String,
)
