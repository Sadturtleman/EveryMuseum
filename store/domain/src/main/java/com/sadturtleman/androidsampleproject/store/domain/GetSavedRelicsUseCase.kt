package com.sadturtleman.androidsampleproject.store.domain

import com.sadturtleman.androidsampleproject.common.domain.saved.SavedRelicRepository
import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 보관함 목록 조회 (Figma: 06 · 보관함).
 *
 * 저장 시점의 표시값을 그대로 쓰므로 네트워크를 타지 않는다.
 * 정렬은 화면이 고르는 값이라 여기서 정하지 않고 목록만 흘려보낸다.
 */
class GetSavedRelicsUseCase @Inject constructor(
    private val savedRelicRepository: SavedRelicRepository,
) {
    operator fun invoke(): Flow<List<SavedRelicVO>> = savedRelicRepository.getSavedRelicsFlow()
}
