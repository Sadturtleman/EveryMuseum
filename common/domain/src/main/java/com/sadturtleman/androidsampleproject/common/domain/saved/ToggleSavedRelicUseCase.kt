package com.sadturtleman.androidsampleproject.common.domain.saved

import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import javax.inject.Inject

/**
 * 북마크 토글. 저장돼 있으면 빼고, 없으면 지금 시각으로 넣는다.
 *
 * 보관함 · 홈 · 검색 결과 · 상세가 모두 이 하나를 쓴다. 보관함은 저장된 것만 보여주므로
 * 결과가 언제나 "해제" 지만, 판단은 저장소가 트랜잭션 안에서 하므로 화면이 미리 정할 필요가 없다.
 *
 * 저장 시점의 표시값을 그대로 담아야 하므로 id 가 아니라 [SavedRelicVO] 를 받는다.
 */
class ToggleSavedRelicUseCase @Inject constructor(
    private val savedRelicRepository: SavedRelicRepository,
) {
    /** @return 저장했으면 true, 해제했으면 false. */
    // 저장 시각은 여기서 찍는다. 해제되는 경우엔 쓰이지 않고 버려진다.
    suspend operator fun invoke(relic: SavedRelicVO): Boolean =
        savedRelicRepository.toggle(relic.copy(savedAt = System.currentTimeMillis()))
}
