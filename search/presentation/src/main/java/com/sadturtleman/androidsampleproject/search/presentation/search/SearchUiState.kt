package com.sadturtleman.androidsampleproject.search.presentation.search

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

/**
 * 검색 화면 상태 (Figma: 최종 → 02 · 검색).
 *
 * 코드 둘러보기 항목은 `GET /openapi/code?parentCode=PS01 / PS08 / GL05 / PS15` 로 채운다.
 */
sealed interface SearchUiState {

    /** 최근 검색어 · 색인어 · 코드 목록을 불러오는 중 */
    data object Loading : SearchUiState

    /** 조회 실패 */
    data class Error(val message: String) : SearchUiState

    /**
     * 조회 성공.
     *
     * @param query 검색 바에 입력된 질의
     * @param recentQueries 최근 검색어. 비면 해당 블록을 그리지 않는다.
     * @param popularIndexWords 인기 색인어(indexWord). 비면 해당 블록을 그리지 않는다.
     * @param codeCategories 코드로 둘러보기 항목
     */
    data class Success(
        val query: String = "",
        val recentQueries: List<String> = emptyList(),
        val popularIndexWords: List<String> = emptyList(),
        val codeCategories: List<CodeCategoryUiModel> = emptyList(),
    ) : SearchUiState
}

/**
 * 코드로 둘러보기 한 줄.
 *
 * @param parentCode 진입 시 `view_code_list` 에 넘길 상위 코드 (예: PS01)
 * @param summary 코드와 하위 예시를 한 줄로 요약한 문구
 */
@Immutable
data class CodeCategoryUiModel(
    val parentCode: String,
    val title: String,
    val summary: String,
    @param:DrawableRes val icon: Int,
)
