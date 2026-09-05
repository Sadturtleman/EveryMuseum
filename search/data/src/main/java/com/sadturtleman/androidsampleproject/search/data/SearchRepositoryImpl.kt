package com.sadturtleman.androidsampleproject.search.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sadturtleman.androidsampleproject.common.data.relic.toQueryMap
import com.sadturtleman.androidsampleproject.common.data.relic.toRelicPageVO
import com.sadturtleman.androidsampleproject.common.data.remote.EmuseumApi
import com.sadturtleman.androidsampleproject.common.di.IoDispatcher
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicQuery
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicVO
import com.sadturtleman.androidsampleproject.common.network.requireSuccess
import com.sadturtleman.androidsampleproject.search.domain.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 검색 구현.
 *
 * 질의어와 필터 코드를 목록 API 조건으로 옮기고([searchQuery]), 페이지를 이어 붙인다.
 * 파싱까지 IO 디스패처에서 돌리고, 실패는 예외로 올린다.
 */
@Singleton
internal class SearchRepositoryImpl @Inject constructor(
    private val emuseumApi: EmuseumApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : SearchRepository {

    override fun searchRelics(
        query: String,
        filterCodes: List<String>,
        pageSize: Int,
    ): Flow<PagingData<RelicVO>> {
        val relicQuery = searchQuery(query, filterCodes, pageSize)
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                // 첫 로드도 딱 한 페이지만. 기본값(pageSize 의 3배)을 쓰면 pageNo 계산이 어긋난다.
                initialLoadSize = pageSize,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { RelicPagingSource(relicQuery, ::loadPage) },
        ).flow
    }

    /** 행은 쓰지 않고 totalCount 만 읽으므로 한 건만 받아온다. */
    override suspend fun countRelics(query: String, filterCodes: List<String>): Int =
        loadPage(searchQuery(query, filterCodes, pageSize = COUNT_ONLY_PAGE_SIZE)).totalCount

    private suspend fun loadPage(query: RelicQuery): RelicPageVO = withContext(ioDispatcher) {
        emuseumApi.getRelicList(query.toQueryMap())
            .requireSuccess()
            .toRelicPageVO(query)
    }

    private companion object {
        const val COUNT_ONLY_PAGE_SIZE = 1
    }
}
