package com.sadturtleman.androidsampleproject.search.domain

import androidx.paging.PagingData
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicVO
import kotlinx.coroutines.flow.Flow

/**
 * 검색 저장소 계약. 구현은 :search:data 에 있다.
 *
 * 홈과 같은 목록 엔드포인트를 쓰지만 필요한 것이 다르다 —
 * 검색은 결과가 수십만 건이라 페이지 단위로 이어 붙여야 하고, 전체 건수도 따로 센다.
 * (홈은 한 페이지만 보므로 [HomeContentRepository] 는 둘 다 갖지 않는다)
 */
interface SearchRepository {

    /**
     * 조건에 맞는 소장품을 페이지 단위로 흘려보낸다. 첫 페이지부터 아래로만 이어 붙는다.
     *
     * @param filterCodes 필터 시트에서 고른 분류 코드. 어느 파라미터에 실을지는 구현이 정한다.
     */
    fun searchRelics(
        query: String,
        filterCodes: List<String>,
        pageSize: Int,
    ): Flow<PagingData<RelicVO>>

    /** 같은 조건에 걸리는 전체 건수. 행은 쓰지 않는다. */
    suspend fun countRelics(query: String, filterCodes: List<String>): Int
}
