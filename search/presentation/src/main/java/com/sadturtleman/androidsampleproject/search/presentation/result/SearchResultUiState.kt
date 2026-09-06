package com.sadturtleman.androidsampleproject.search.presentation.result

import androidx.compose.runtime.Immutable
import com.sadturtleman.androidsampleproject.common.presentation.mvi.UiState

/**
 * 검색 결과 화면 상태 (Figma: 최종 → 03 · 검색 결과).
 *
 * 목록 자체는 여기 없다. 결과가 수십만 건이라 Paging 으로 흘려보내고
 * ([SearchResultViewModel.items]), 로딩 · 에러 · 결과 없음도 Paging 의 LoadState 가 알려준다.
 * 이 상태에는 목록을 둘러싼 값 — 검색 바 · 툴바 · 필터 줄 · 시트 — 만 남는다.
 *
 * @param query 검색 바에 입력된 질의. 아직 실행되지 않은 입력일 수 있다
 *  (실행된 조건은 ViewModel 이 따로 들고 있다).
 * @param appliedFilters 적용된 필터 칩. 비면 필터 줄에 진입 pill 만 그린다.
 * @param totalCount 조건에 걸린 전체 건수. 아직 세는 중이면 null 이라 "N건" 을 그리지 않는다.
 * @param isFilterSheetVisible 필터 시트는 목록을 다시 불러오는 동안에도 떠 있을 수 있어
 *  목록 상태와 나란히 두지 않고 화면 레벨 값으로 둔다.
 * @param filterTabCode "코드로 둘러보기" 로 들어온 경우 펴야 할 필터 갈래. 시트를 여는 쪽이 읽는다.
 */
@Immutable
data class SearchResultUiState(
    val query: String = "",
    val appliedFilters: List<AppliedFilterUiModel> = emptyList(),
    val totalCount: Int? = null,
    val isFilterSheetVisible: Boolean = false,
    val filterTabCode: String? = null,
) : UiState

/**
 * 적용된 필터 칩 하나.
 *
 * @param code 목록 API 로 넘어가는 코드값
 * @param label 사용자에게 보이는 이름 (nameKr)
 */
@Immutable
data class AppliedFilterUiModel(
    val code: String,
    val label: String,
)
