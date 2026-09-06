package com.sadturtleman.androidsampleproject.home.data

import com.sadturtleman.androidsampleproject.common.data.relic.toQueryMap
import com.sadturtleman.androidsampleproject.common.data.relic.toRelicPageVO
import com.sadturtleman.androidsampleproject.common.data.remote.EmuseumApi
import com.sadturtleman.androidsampleproject.common.di.IoDispatcher
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicQuery
import com.sadturtleman.androidsampleproject.common.network.requireSuccess
import com.sadturtleman.androidsampleproject.home.domain.HomeContentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 홈 본문 구현.
 *
 * 시대 코드를 목록 API 의 nationalityCode 로 옮기는 것이 이 저장소가 하는 유일한 판단이다.
 * 파싱까지 IO 디스패처에서 돌리고, 실패는 예외로 올린다.
 */
@Singleton
internal class HomeContentRepositoryImpl @Inject constructor(
    private val emuseumApi: EmuseumApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : HomeContentRepository {

    override suspend fun getHomeRelics(
        eraCode: String?,
        pageSize: Int,
    ): RelicPageVO = withContext(ioDispatcher) {
        val query = RelicQuery(
            pageNo = RelicQuery.FIRST_PAGE,
            numOfRows = pageSize,
            nationalityCode = eraCode,
        )
        emuseumApi.getRelicList(query.toQueryMap())
            .requireSuccess()
            .toRelicPageVO(query)
    }
}
