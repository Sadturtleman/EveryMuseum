package com.sadturtleman.androidsampleproject.detail.data

import com.sadturtleman.androidsampleproject.common.di.IoDispatcher
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicDetailVO
import com.sadturtleman.androidsampleproject.common.network.EmuseumApiException
import com.sadturtleman.androidsampleproject.common.network.requireSuccess
import com.sadturtleman.androidsampleproject.detail.data.remote.EmuseumDetailApi
import com.sadturtleman.androidsampleproject.detail.domain.RelicDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 상세 조회 구현.
 *
 * 파싱까지 포함해 IO 디스패처에서 돌린다(상세 응답은 이미지 · 연관 목록까지 붙어 목록보다 크다).
 * 실패는 예외로 올린다 — HTTP 오류는 Retrofit 이, 본문 resultCode 실패는 requireSuccess 가 던진다.
 */
@Singleton
internal class RelicDetailRepositoryImpl @Inject constructor(
    private val emuseumDetailApi: EmuseumDetailApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : RelicDetailRepository {

    override suspend fun getRelicDetail(id: String): RelicDetailVO = withContext(ioDispatcher) {
        val response = emuseumDetailApi.getRelicDetail(id).requireSuccess()
        // 없는 id 는 오류가 아니라 빈 목록으로 온다. 화면이 에러 상태로 처리하도록 예외로 바꾼다.
        if (response.rows().isEmpty()) {
            throw EmuseumApiException(CODE_NOT_FOUND, "소장품을 찾을 수 없습니다. (id=$id)")
        }
        response.toRelicDetailVO()
    }

    private companion object {
        const val CODE_NOT_FOUND = "NOT_FOUND"
    }
}
