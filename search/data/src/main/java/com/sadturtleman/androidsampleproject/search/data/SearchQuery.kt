package com.sadturtleman.androidsampleproject.search.data

import com.sadturtleman.androidsampleproject.common.entity.relic.RelicQuery

/**
 * 검색 화면의 입력(질의어 + 필터 칩)을 목록 API 조회 조건으로 옮긴다.
 *
 * 코드는 `PS06...`(시대) · `PS08...`(재질) 처럼 접두어로 갈래가 정해져 있어,
 * 화면은 고른 코드 목록만 넘기면 되고 어느 파라미터에 실을지는 여기서 정한다.
 *
 * 같은 갈래를 여러 개 고른 경우 첫 코드만 쓴다 —
 * 목록 API 가 파라미터당 코드 하나만 받기 때문이다(OR 검색은 지원하지 않는다).
 *
 * 조회하는 곳이 둘(목록 페이지네이션 · 예상 건수)이라 규칙을 한 자리에 모아 둔다.
 * 화면은 고른 코드 목록만 넘기고, 어느 파라미터에 실을지는 이 구현 계층이 안다.
 */
internal fun searchQuery(
    query: String,
    filterCodes: List<String>,
    pageSize: Int,
): RelicQuery = RelicQuery(
    pageNo = RelicQuery.FIRST_PAGE,
    numOfRows = pageSize,
    name = query.trim().takeIf { it.isNotEmpty() },
    nationalityCode = filterCodes.firstWithPrefix(PREFIX_NATIONALITY),
    materialCode = filterCodes.firstWithPrefix(PREFIX_MATERIAL),
    museumCode = filterCodes.firstWithPrefix(PREFIX_MUSEUM),
    purposeCode = filterCodes.firstWithPrefix(PREFIX_PURPOSE),
    sizeRangeCode = filterCodes.firstWithPrefix(PREFIX_SIZE_RANGE),
)

private fun List<String>.firstWithPrefix(prefix: String): String? =
    firstOrNull { it.startsWith(prefix) }

private const val PREFIX_NATIONALITY = "PS06"
private const val PREFIX_MATERIAL = "PS08"
private const val PREFIX_MUSEUM = "PS01"
private const val PREFIX_PURPOSE = "PS09"
private const val PREFIX_SIZE_RANGE = "PS15"
