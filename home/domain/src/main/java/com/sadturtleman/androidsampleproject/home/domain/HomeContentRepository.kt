package com.sadturtleman.androidsampleproject.home.domain

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO

/**
 * 홈 본문 저장소 계약. 구현은 :home:data 에 있다.
 *
 * 홈은 "오늘의 소장품" 한 페이지만 필요하다 — 끝까지 이어 보는 목록이 아니라서
 * 페이지네이션도, 전체 건수도 쓰지 않는다. 그래서 검색과 같은 엔드포인트를 쓰더라도
 * 계약은 각자 자기가 필요한 만큼만 가진다.
 */
interface HomeContentRepository {

    /**
     * 오늘의 소장품 한 페이지.
     *
     * @param eraCode 시대 칩으로 고른 국적/시대 코드. null 이면 전체다.
     * @param pageSize 받아올 행 수. 히어로 1 + 그리드 몇 줄이면 충분하다.
     */
    suspend fun getHomeRelics(eraCode: String?, pageSize: Int): RelicPageVO
}
