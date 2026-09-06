package com.sadturtleman.androidsampleproject.common.domain.code

import com.sadturtleman.androidsampleproject.common.entity.code.CodeVO

/**
 * 분류 코드표 계약. 구현은 :common:data 에 있다.
 *
 * 소장품 조회와 달리 홈(시대 칩)과 검색(필터 탭)이 함께 쓰므로 한 feature 가 소유하지 않는다.
 * 코드 체계는 거의 바뀌지 않아, 나중에 캐시를 넣는다면 이 계약 뒤가 그 자리다.
 */
interface CodeRepository {

    /** 상위 코드의 직계 자식 목록. [parentCode] 는 PS08 · PS06001 같은 값이다. */
    suspend fun getCodes(parentCode: String): List<CodeVO>
}
