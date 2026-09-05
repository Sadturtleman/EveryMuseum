package com.sadturtleman.androidsampleproject.search.domain

import androidx.paging.PagingData
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicQuery
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicVO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 검색 결과 조회 (Figma: 03 · 검색 결과).
 *
 * 결과가 수십만 건이라 한 번에 받지 않고 페이지 단위로 이어 붙인다.
 * 조회 실패는 [PagingData] 의 LoadState 로 전달되어 호출부(ViewModel)가 화면 상태로 옮긴다.
 *
 * 전체 건수는 [CountRelicsUseCase] 가 따로 센다 — PagingData 는 건수를 싣지 않는다.
 */
class SearchRelicsUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    operator fun invoke(
        query: String,
        filterCodes: List<String> = emptyList(),
        pageSize: Int = RelicQuery.DEFAULT_PAGE_SIZE,
    ): Flow<PagingData<RelicVO>> = searchRepository.searchRelics(
        query = query,
        filterCodes = filterCodes,
        pageSize = pageSize,
    )
}
