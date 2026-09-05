package com.sadturtleman.androidsampleproject.common.data.code

import com.sadturtleman.androidsampleproject.common.data.relic.toCodeVO
import com.sadturtleman.androidsampleproject.common.data.remote.EmuseumApi
import com.sadturtleman.androidsampleproject.common.di.IoDispatcher
import com.sadturtleman.androidsampleproject.common.domain.code.CodeRepository
import com.sadturtleman.androidsampleproject.common.entity.code.CodeVO
import com.sadturtleman.androidsampleproject.common.network.requireSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 분류 코드표 구현.
 *
 * 파싱까지 IO 디스패처에서 돌리고, 실패는 예외로 올린다
 * (HTTP 오류는 Retrofit 이, 본문 resultCode 실패는 requireSuccess 가 던진다).
 */
@Singleton
internal class CodeRepositoryImpl @Inject constructor(
    private val emuseumApi: EmuseumApi,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CodeRepository {

    override suspend fun getCodes(parentCode: String): List<CodeVO> = withContext(ioDispatcher) {
        emuseumApi.getCodeList(parentCode, numOfRows = CODE_PAGE_SIZE)
            .requireSuccess()
            .rows()
            .map { it.toCodeVO() }
            .sortedBy { it.code }
    }

    private companion object {
        const val CODE_PAGE_SIZE = 200
    }
}
