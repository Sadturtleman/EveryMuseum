package com.sadturtleman.androidsampleproject.common.domain.saved

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 저장된 소장품 id 집합.
 *
 * 카드의 북마크 아이콘 상태를 정하는 값이라 홈 · 검색 결과 · 상세가 모두 구독한다.
 * 목록 자체가 아니라 id 만 보므로, 표시값만 바뀐 저장은 화면을 다시 그리지 않는다.
 */
class GetSavedRelicIdsUseCase @Inject constructor(
    private val savedRelicRepository: SavedRelicRepository,
) {
    operator fun invoke(): Flow<Set<String>> = savedRelicRepository.getSavedRelicsFlow()
        .map { relics -> relics.mapTo(HashSet(relics.size)) { it.id } }
        .distinctUntilChanged()
}
