package com.sadturtleman.androidsampleproject.common.domain.saved

import com.sadturtleman.androidsampleproject.common.entity.saved.SavedRelicVO
import kotlinx.coroutines.flow.Flow

/**
 * 보관함 저장소 계약. 구현은 :store:data 에 있다.
 *
 * 저장/해제는 보관함만이 아니라 홈 · 검색 결과 카드와 상세에서도 일어나므로 계약은 common 에 둔다.
 * (feature 소유 저장소지만 여러 화면이 쓰는 것 — 구현만 :store:data)
 */
interface SavedRelicRepository {

    /** 저장 목록. 저장/해제가 일어나면 다시 흘러온다. */
    fun getSavedRelicsFlow(): Flow<List<SavedRelicVO>>

    /**
     * 저장돼 있으면 해제하고, 없으면 [relic] 을 저장한다.
     *
     * 조회 → 판단 → 쓰기를 밖에서 나눠 하면 그 사이에 다른 토글이 끼어들 수 있어
     * 판단까지 구현의 저장 트랜잭션 안에서 처리한다.
     *
     * @return 저장했으면 true, 해제했으면 false. 화면이 결과를 알리는 데 쓴다.
     */
    suspend fun toggle(relic: SavedRelicVO): Boolean
}
