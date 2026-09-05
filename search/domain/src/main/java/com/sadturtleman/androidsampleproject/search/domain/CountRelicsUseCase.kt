package com.sadturtleman.androidsampleproject.search.domain

import javax.inject.Inject

/**
 * 같은 조건에 걸리는 전체 건수.
 *
 * 검색 결과의 "N건" 과 필터 시트의 "N건 결과 보기" 가 쓴다.
 */
class CountRelicsUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    suspend operator fun invoke(
        query: String,
        filterCodes: List<String> = emptyList(),
    ): Int = searchRepository.countRelics(query = query, filterCodes = filterCodes)
}
