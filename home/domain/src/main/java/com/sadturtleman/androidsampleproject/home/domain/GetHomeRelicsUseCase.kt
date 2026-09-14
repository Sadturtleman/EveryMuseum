package com.sadturtleman.androidsampleproject.home.domain

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO
import com.sadturtleman.androidsampleproject.featureflag.domain.FeatureFlagProvider
import com.sadturtleman.androidsampleproject.featureflag.domain.FlagKey
import javax.inject.Inject

/**
 * 홈의 "오늘의 소장품" 조회 (Figma: 01 · 홈).
 *
 * 시대 칩 선택은 목록 API 의 nationalityCode 파라미터가 된다(변환은 :home:data 가 한다).
 * 홈은 첫 페이지만 보여주므로 페이징 인자를 노출하지 않는다 —
 * 몇 줄을 받아 올지는 [FlagKey.HomePageSize] 가 정한다(리모트가 모르면 기본값 13).
 *
 * 플래그를 화면이 아니라 여기서 읽는 이유는 그것이 조회 조건이기 때문이다.
 * :featureflag:domain 이 순수 코틀린이라 이 모듈에서도 그대로 주입받는다.
 *
 * @param eraCode 선택된 시대 코드. null 이면 "전체" 다.
 */
class GetHomeRelicsUseCase @Inject constructor(
    private val homeContentRepository: HomeContentRepository,
    private val featureFlags: FeatureFlagProvider,
) {
    suspend operator fun invoke(eraCode: String? = null): RelicPageVO =
        homeContentRepository.getHomeRelics(
            eraCode = eraCode,
            pageSize = featureFlags.get(FlagKey.HomePageSize),
        )
}
