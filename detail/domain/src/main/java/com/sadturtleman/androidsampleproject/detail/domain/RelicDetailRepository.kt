package com.sadturtleman.androidsampleproject.detail.domain

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicDetailVO

/**
 * 소장품 상세 저장소 계약. 구현은 :detail:data 에 있다.
 *
 * 상세는 이 feature 만 조회하므로 계약도 여기 둔다.
 * (여러 화면이 함께 쓰는 목록 · 코드 조회는 :common:domain 의 RelicRepository 다)
 */
interface RelicDetailRepository {

    /** 소장품 상세 + 공개 이미지 + 연관 소장품. 없는 id 면 예외를 던진다. */
    suspend fun getRelicDetail(id: String): RelicDetailVO
}
