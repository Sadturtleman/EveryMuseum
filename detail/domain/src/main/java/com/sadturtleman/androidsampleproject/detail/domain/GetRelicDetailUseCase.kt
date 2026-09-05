package com.sadturtleman.androidsampleproject.detail.domain

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicDetailVO
import javax.inject.Inject

/**
 * 소장품 상세 조회 (Figma: 05 · 소장품 상세).
 *
 * 상세 화면에서만 쓰는 조회라 계약(RelicDetailRepository)과 구현(:detail:data) 모두 이 feature 가 소유한다.
 * 없는 id(딥링크 오타 등)면 저장소가 예외를 던지고, 화면은 그걸 에러 상태로 옮긴다.
 */
class GetRelicDetailUseCase @Inject constructor(
    private val relicDetailRepository: RelicDetailRepository,
) {
    suspend operator fun invoke(id: String): RelicDetailVO =
        relicDetailRepository.getRelicDetail(id)
}
