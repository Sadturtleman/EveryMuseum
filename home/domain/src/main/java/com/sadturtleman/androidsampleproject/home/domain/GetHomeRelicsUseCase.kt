package com.sadturtleman.androidsampleproject.home.domain

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO
import javax.inject.Inject

/**
 * 홈의 "오늘의 소장품" 조회 (Figma: 01 · 홈).
 *
 * 시대 칩 선택은 목록 API 의 nationalityCode 파라미터가 된다(변환은 :home:data 가 한다).
 * 홈은 첫 페이지만 보여주므로 페이징 인자를 노출하지 않는다.
 *
 * @param eraCode 선택된 시대 코드. null 이면 "전체" 다.
 */
class GetHomeRelicsUseCase @Inject constructor(
    private val homeContentRepository: HomeContentRepository,
) {
    suspend operator fun invoke(eraCode: String? = null): RelicPageVO =
        homeContentRepository.getHomeRelics(eraCode = eraCode, pageSize = HOME_PAGE_SIZE)

    private companion object {
        /** 히어로 1 + 2열 그리드 몇 줄이면 충분하다. */
        const val HOME_PAGE_SIZE = 13
    }
}
