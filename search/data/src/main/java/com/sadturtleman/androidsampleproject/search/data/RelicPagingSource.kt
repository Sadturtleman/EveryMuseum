package com.sadturtleman.androidsampleproject.search.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicQuery
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicVO

/**
 * 목록 API 의 `pageNo` 를 Paging 키로 쓰는 페이지 소스.
 *
 * [params] 의 loadSize 를 쓰지 않고 [query] 의 numOfRows 를 그대로 쓴다.
 * Paging 은 첫 로드에서만 pageSize 의 배수를 요청하는데, 그 값을 numOfRows 로 넘기면
 * 다음 페이지 번호 계산이 어긋나 행이 건너뛰어진다.
 * (호출부가 PagingConfig 의 initialLoadSize 를 pageSize 와 같게 맞춘다)
 */
internal class RelicPagingSource(
    private val query: RelicQuery,
    private val loadPage: suspend (RelicQuery) -> RelicPageVO,
) : PagingSource<Int, RelicVO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RelicVO> {
        val pageNo = params.key ?: RelicQuery.FIRST_PAGE
        return runCatching { loadPage(query.copy(pageNo = pageNo)) }.fold(
            onSuccess = { page ->
                LoadResult.Page(
                    data = page.items,
                    // 항상 첫 페이지에서 시작해 아래로만 이어 붙는다.
                    prevKey = null,
                    nextKey = if (page.hasNext) pageNo + 1 else null,
                )
            },
            onFailure = { throwable -> LoadResult.Error(throwable) },
        )
    }

    /**
     * 새로고침은 언제나 첫 페이지부터 한다.
     *
     * 목록 API 는 정렬 기준을 노출하지 않아 같은 조건이라도 페이지 경계가 그대로라는 보장이 없다.
     * 보던 자리를 키로 되짚으면 오히려 행이 겹치거나 빠질 수 있다.
     */
    override fun getRefreshKey(state: PagingState<Int, RelicVO>): Int? = null
}
