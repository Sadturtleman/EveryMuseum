package com.sadturtleman.androidsampleproject.common.entity.relic

import kotlinx.serialization.Serializable

/**
 * 소장품 목록 한 건 (e뮤지엄 `GET /openapi/list` 의 `<data>` 하나).
 *
 * 응답이 `<item key="..." value="..."/>` 쌍이라 없는 필드는 아예 오지 않는다.
 * 그래서 조회 키인 [id] 를 빼면 전부 nullable 이다.
 *
 * @param id 소장품 고유 id. 상세 조회(`/openapi/detail?id=`)의 키다.
 * @param nameKr 한글명. 목록 카드의 제목이다.
 * @param nameCn 한자명
 * @param museumName2 소장기관명 (예: 국립중앙박물관)
 * @param museumName3 소장기관 하위 구분 (예: 본관)
 * @param nationalityCode 국적/시대 코드 (PS06 계열)
 * @param imgThumUriS 목록용 썸네일. URL 안에 이미지 전용 serviceKey 가 이미 들어 있다.
 */
@Serializable
data class RelicVO(
    val id: String = "",
    val nameKr: String? = null,
    val nameCn: String? = null,
    val name: String? = null,
    val relicNo: String? = null,
    val museumName1: String? = null,
    val museumName2: String? = null,
    val museumName3: String? = null,
    val museumCode: String? = null,
    val nationalityCode: String? = null,
    val materialCode: String? = null,
    val purposeCode: String? = null,
    val sizeRangeCode: String? = null,
    val indexWord: String? = null,
    val imgUri: String? = null,
    val imgThumUriS: String? = null,
    val imgThumUriM: String? = null,
    val imgThumUriL: String? = null,
) {
    /** 소장기관 표시용 한 줄. 예: `국립중앙박물관 · 본관` */
    val museumLabel: String
        get() = listOfNotNull(museumName2, museumName3)
            .filter { it.isNotBlank() }
            .joinToString(" · ")
}

/**
 * 목록 조회 한 페이지.
 *
 * @param totalCount 필터를 적용한 전체 건수. 검색 결과 화면의 "N건" 이 이 값이다.
 */
@Serializable
data class RelicPageVO(
    val items: List<RelicVO> = emptyList(),
    val totalCount: Int = 0,
    val pageNo: Int = 1,
    val numOfRows: Int = 0,
) {
    /** 다음 페이지가 남아 있는지. 페이지네이션 종료 판단에 쓴다. */
    val hasNext: Boolean get() = pageNo * numOfRows < totalCount
}

/**
 * 소장품 상세 (`GET /openapi/detail?id=`).
 *
 * 응답은 `<list>`(본문) · `<imageList>`(공개 이미지) · `<relationList>`(연관 소장품) 세 묶음이며
 * 본문에는 목록 응답에 없는 이름 필드(materialName · purposeName · nationalityName · sizeInfo)가 더 온다.
 */
@Serializable
data class RelicDetailVO(
    val relic: RelicVO = RelicVO(),
    val nationalityName1: String? = null,
    val nationalityName2: String? = null,
    val materialName1: String? = null,
    val materialName2: String? = null,
    val purposeName1: String? = null,
    val purposeName2: String? = null,
    val purposeName3: String? = null,
    val purposeName4: String? = null,
    val sizeRangeName: String? = null,
    val sizeInfo: String? = null,
    val designationName1: String? = null,
    val description: String? = null,
    val images: List<RelicImageVO> = emptyList(),
    val relations: List<RelicVO> = emptyList(),
)

/** 공개 이미지 한 장 (`<imageList>` 의 `<data>`). */
@Serializable
data class RelicImageVO(
    val imgId: String = "",
    val imgOrder: Int = 0,
    val imgUri: String? = null,
    val imgThumUriS: String? = null,
    val imgThumUriM: String? = null,
    val imgThumUriL: String? = null,
)
