package com.sadturtleman.androidsampleproject.common.data.relic

import com.sadturtleman.androidsampleproject.common.network.EmuseumResponse
import com.sadturtleman.androidsampleproject.common.entity.code.CodeVO
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicImageVO
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicPageVO
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicQuery
import com.sadturtleman.androidsampleproject.common.entity.relic.RelicVO

/*
 * key/value 한 행([Map]) -> VO 변환.
 *
 * 응답은 값이 있는 item 만 내려오므로 없는 키는 그대로 null 이 된다.
 * 키 이름은 e뮤지엄 응답 스펙 그대로 쓴다.
 */

/**
 * 목록 응답의 한 행을 VO 로 옮긴다.
 *
 * 상세 응답의 본문 · 연관 소장품도 같은 모양이라 :detail:data 가 이 변환을 그대로 쓴다.
 */
fun Map<String, String>.toRelicVO(): RelicVO = RelicVO(
    id = this["id"].orEmpty(),
    nameKr = this["nameKr"],
    nameCn = this["nameCn"],
    name = this["name"],
    relicNo = this["relicNo"],
    museumName1 = this["museumName1"],
    museumName2 = this["museumName2"],
    museumName3 = this["museumName3"],
    museumCode = this["museumCode"],
    nationalityCode = this["nationalityCode"],
    materialCode = this["materialCode"],
    purposeCode = this["purposeCode"],
    sizeRangeCode = this["sizeRangeCode"],
    indexWord = this["indexWord"],
    imgUri = imageUrl("imgUri"),
    imgThumUriS = imageUrl("imgThumUriS"),
    imgThumUriM = imageUrl("imgThumUriM"),
    imgThumUriL = imageUrl("imgThumUriL"),
)

/** 공개 이미지 한 행. 상세 응답의 `imageList` 에만 오지만 변환 규칙은 목록 이미지와 같다. */
fun Map<String, String>.toRelicImageVO(): RelicImageVO = RelicImageVO(
    imgId = this["imgId"].orEmpty(),
    imgOrder = this["imgOrder"]?.toIntOrNull() ?: 0,
    imgUri = imageUrl("imgUri"),
    imgThumUriS = imageUrl("imgThumUriS"),
    imgThumUriM = imageUrl("imgThumUriM"),
    imgThumUriL = imageUrl("imgThumUriL"),
)

/**
 * 이미지 주소를 https 로 맞춘다.
 *
 * 응답은 `http://www.emuseum.go.kr/...` 로 오는데 안드로이드는 기본적으로 평문 HTTP 를 막는다.
 * 같은 호스트가 https 로도 같은 이미지를 주므로, 앱 전체에 평문을 허용하는 대신 여기서 바꾼다.
 */
internal fun Map<String, String>.imageUrl(key: String): String? = this[key]?.let { url ->
    if (url.startsWith(HTTP_PREFIX)) HTTPS_PREFIX + url.removePrefix(HTTP_PREFIX) else url
}

private const val HTTP_PREFIX = "http://"
private const val HTTPS_PREFIX = "https://"

/** 분류 코드 한 행. */
fun Map<String, String>.toCodeVO(): CodeVO = CodeVO(
    code = this["code"].orEmpty(),
    parentCode = this["parentCode"],
    nameKr = this["nameKr"],
    nameEn = this["nameEn"],
    nameCn = this["nameCn"],
    level = this["level"]?.toIntOrNull() ?: 0,
)

/** 목록 응답 한 페이지. 홈 · 검색이 같은 엔드포인트를 쓰므로 변환도 공유한다. */
fun EmuseumResponse.toRelicPageVO(query: RelicQuery): RelicPageVO = RelicPageVO(
    items = rows().map { it.toRelicVO() },
    totalCount = totalCount,
    // 목록 응답의 pageNo · numOfRows 를 그대로 쓰되, 값이 비면 요청값으로 되돌린다.
    pageNo = pageNo.takeIf { it > 0 } ?: query.pageNo,
    numOfRows = numOfRows.takeIf { it > 0 } ?: query.numOfRows,
)

/**
 * 조회 조건을 쿼리 파라미터로 편다.
 * 값이 없는 조건은 키 자체를 빼야 한다(빈 값으로 넘기면 0건이 된다).
 */
fun RelicQuery.toQueryMap(): Map<String, String> = buildMap {
    put("pageNo", pageNo.toString())
    put("numOfRows", numOfRows.toString())
    putIfNotBlank("name", name)
    putIfNotBlank("indexWord", indexWord)
    putIfNotBlank("nationalityCode", nationalityCode)
    putIfNotBlank("materialCode", materialCode)
    putIfNotBlank("museumCode", museumCode)
    putIfNotBlank("purposeCode", purposeCode)
    putIfNotBlank("sizeRangeCode", sizeRangeCode)
}

private fun MutableMap<String, String>.putIfNotBlank(key: String, value: String?) {
    val trimmed = value?.trim().orEmpty()
    if (trimmed.isNotEmpty()) put(key, trimmed)
}
